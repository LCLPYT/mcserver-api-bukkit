/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.serverapi.util.ServerTranslation;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;
import work.lclpnet.translations.Translations;
import work.lclpnet.translations.io.JarTranslationLocator;
import work.lclpnet.translations.io.ResourceTranslationLoader;
import work.lclpnet.translations.network.LCLPNetworkTranslationLoader;
import work.lclpnet.translations.network.LCLPTranslationAPI;
import work.lclpnet.translations.util.ILogger;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;

public class BukkitServerTranslation {

    public static void init(JavaPlugin plugin, ILogger logger) throws IOException {
        Class<?> clazz = BukkitServerTranslation.class;

        JarTranslationLocator locator = new JarTranslationLocator(clazz, logger, Collections.singletonList("resource/bukkit/lang/"));
        ResourceTranslationLoader loader = new ResourceTranslationLoader(locator, plugin::getResource, logger);

        Translations.loadFrom(loader);
    }

    public static String getTranslation(Player player, String key, Object... substitutes) {
        return ServerTranslation.getTranslation(player.getUniqueId().toString(), player.getLocale(), key, substitutes);
    }

    public static void fetchTranslationsForApp(String appName, Logger originalLogger) {
        ILogger logger = new BukkitLogger(originalLogger);
        APIAccess access = MCServerBukkit.getAPI().getAPIAccess();
        if (access == null) throw new IllegalStateException("API access is not yet defined. (called to early)");

        LCLPTranslationAPI api = new LCLPTranslationAPI(access);
        LCLPNetworkTranslationLoader loader = new LCLPNetworkTranslationLoader(Collections.singletonList(appName), null, api, logger);
        try {
            Translations.loadAsyncFrom(loader).thenAccept(ignored -> {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
