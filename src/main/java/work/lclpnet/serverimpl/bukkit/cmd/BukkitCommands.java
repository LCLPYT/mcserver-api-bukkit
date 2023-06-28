/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.cmd;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import work.lclpnet.serverapi.util.ServerContext;

public class BukkitCommands {

    public static void register(JavaPlugin plugin, ServerContext context) {
        registerCommand(plugin, "mclink", new CommandMCLink(context));
        registerCommand(plugin, "stats", new CommandStats(context));
        registerCommand(plugin, "language", new CommandLanguage(context));
    }

    private static void registerCommand(JavaPlugin plugin, String name, CommandExecutor executor) {
        if(name == null) throw new NullPointerException("Command name is null.");

        PluginCommand cmd = plugin.getCommand(name);
        if(cmd == null) throw new NullPointerException("There is no command with name '" + name + "'registered.");

        cmd.setExecutor(executor);
    }

}
