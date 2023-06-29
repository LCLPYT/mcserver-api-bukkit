/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import work.lclpnet.translations.loader.translation.MultiSourceTranslationLoader;
import work.lclpnet.translations.loader.translation.SPITranslationLoader;
import work.lclpnet.translations.model.LanguageCollection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BukkitSPITranslationLoader extends MultiSourceTranslationLoader {

    @Override
    protected void collectFutures(List<CompletableFuture<? extends LanguageCollection>> futures) {
        final Set<ClassLoader> loaders = new HashSet<>();
        final Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

        for (Plugin plugin : plugins) {
            loaders.add(plugin.getClass().getClassLoader());
        }

        for (ClassLoader loader : loaders) {
            SPITranslationLoader spiLoader = new SPITranslationLoader(loader);
            futures.add(spiLoader.load());
        }
    }
}
