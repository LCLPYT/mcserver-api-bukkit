/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.util;

import org.bukkit.entity.Player;
import work.lclpnet.serverapi.msg.ServerTranslations;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;

import java.util.concurrent.CompletableFuture;

public class BukkitServerTranslation {

    private final ServerTranslations serverTranslations;

    public BukkitServerTranslation(ServerTranslations serverTranslations) {
        this.serverTranslations = serverTranslations;
    }

    public CompletableFuture<Void> init() {
        return serverTranslations.reloadTranslations();
    }

    public String translate(Player player, String key, Object... substitutes) {
        return serverTranslations.getTranslation(player.getUniqueId().toString(), player.getLocale(), key, substitutes);
    }

    /**
     * Translates a string for a given player.
     * @param player The player.
     * @param key The translation key.
     * @param substitutes Substitutes to replace in the string
     * @return The translated message.
     */
    public static String getTranslation(Player player, String key, Object... substitutes) {
        BukkitServerTranslation translation = MCServerBukkit.getTranslations();
        if (translation == null) throw new IllegalStateException("Plugin not yet loaded.");

        return translation.translate(player, key, substitutes);
    }

    public ServerTranslations getServerTranslations() {
        return serverTranslations;
    }
}
