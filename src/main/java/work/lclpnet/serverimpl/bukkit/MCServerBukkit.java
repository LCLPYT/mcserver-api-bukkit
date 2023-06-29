/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.api.APIAuthAccess;
import work.lclpnet.lclpnetwork.util.Utils;
import work.lclpnet.serverapi.MCServerAPI;
import work.lclpnet.serverapi.msg.ServerTranslations;
import work.lclpnet.serverapi.util.ServerCache;
import work.lclpnet.serverapi.util.ServerContext;
import work.lclpnet.serverimpl.bukkit.cmd.BukkitCommands;
import work.lclpnet.serverimpl.bukkit.event.EventListener;
import work.lclpnet.serverimpl.bukkit.util.BukkitSPITranslationLoader;
import work.lclpnet.serverimpl.bukkit.util.BukkitServerTranslation;
import work.lclpnet.storage.LocalLCLPStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MCServerBukkit extends JavaPlugin implements ServerContext {

    public static final String PLUGIN_NAME = "MCServerAPI";
    public static final String pre = String.format("%s%s> %s", ChatColor.BLUE, PLUGIN_NAME, ChatColor.GRAY);
    private static MCServerBukkit plugin = null;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final ServerCache serverCache = new ServerCache();
    private MCServerAPI api = null;
    private BukkitServerTranslation translations = null;
    private boolean testMode = false;

    @Override
    public void onLoad() {
        MCServerBukkit.plugin = this;

        String test = System.getProperty("mcsapi.test-mode");
        if (test != null) {
            testMode = test.equalsIgnoreCase("true") || test.equalsIgnoreCase("yes") || test.equals("1");
            if (testMode)
                Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Enabling test mode... Stats will not be saved.");
        }

        loadConfig();

        String token;
        try {
            token = loadToken();
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getServer().shutdown();
            return;
        }

        APIAuthAccess authAccess = new APIAuthAccess(token);
        authAccess.setHost(Config.live ? Config.liveHost : Config.stagingHost);
        authAccess.setCustomExecutor(executor);

        try {
            authAccess = APIAccess.withAuthCheck(authAccess).join();
        } catch (CompletionException e) {
            Bukkit.getServer().shutdown();
            Bukkit.getConsoleSender().sendMessage(String.format("%s%sCould not login to LCLPNetwork... The server will shut down.", pre, ChatColor.RED));
            throw e;
        }

        api = new MCServerAPI(authAccess);
        Bukkit.getConsoleSender().sendMessage(String.format("%s%sLogged into LCLPNetwork successfully.", pre, ChatColor.GREEN));

        serverCache.init(api);

        loadTranslations();
    }

    private void loadTranslations() {
        BukkitSPITranslationLoader loader = new BukkitSPITranslationLoader();

        try {
            ServerTranslations serverTranslations = new ServerTranslations(serverCache, loader);

            translations = new BukkitServerTranslation(serverTranslations);
            translations.init().join();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Could not initialize translation service", e);
        }
    }

    @Override
    public void onEnable() {
        MCServerBukkit.plugin = this;

        registerListeners();
        BukkitCommands.register(this, this);

        cacheOnlinePlayers();

        Bukkit.getConsoleSender().sendMessage(String.format("%s%sPlugin enabled.", pre, ChatColor.GREEN));
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("Trying to shutdown MCServer executor... (waiting up to 30 seconds)");

        executor.shutdown();
        try {
            if(!executor.awaitTermination(30, TimeUnit.SECONDS))
                throw new IllegalStateException("MCServer executor was terminated while some tasks were still active.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage(String.format("%s%sPlugin disabled.", pre, ChatColor.RED));
    }

    private void cacheOnlinePlayers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        players.forEach(player -> serverCache.refreshPlayer(api, player.getUniqueId().toString()));
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EventListener(serverCache), plugin);
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        Config.load();
    }

    private String loadToken() throws IOException {
        String appName = Config.appName;
        if(appName == null) throw new IllegalStateException("Application name was not set inside of config.yml!");

        File dir = LocalLCLPStorage.getDirectory(
                appName,
                "access_tokens",
                Config.live ? "live" : "staging",
                "dedicated_server");

        File tokenFile = new File(dir, "lclpnetwork.token");
        if(!tokenFile.exists()) throw new FileNotFoundException(String.format("'%s' does not exist!", tokenFile.getAbsolutePath()));

        try (InputStream in = Files.newInputStream(tokenFile.toPath())) {
            return Utils.toString(in, StandardCharsets.UTF_8);
        }
    }

    @Override
    public ServerCache getCache() {
        return serverCache;
    }

    public static boolean isTestMode() {
        return getPlugin().testMode;
    }

    public static MCServerBukkit getPlugin() {
        return plugin;
    }

    public static MCServerAPI getAPI() {
        return getPlugin().api;
    }

    public static BukkitServerTranslation getTranslations() {
        return getPlugin().translations;
    }
}
