/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.translations.loader.TranslationProvider;
import work.lclpnet.translations.loader.language.ClassLoaderLanguageLoader;
import work.lclpnet.translations.loader.language.LanguageLoader;

import java.util.Collections;
import java.util.List;

public class BuiltinTranslationProvider implements TranslationProvider {

    private final Logger logger = LoggerFactory.getLogger(BuiltinTranslationProvider.class);

    @Override
    public LanguageLoader create() {
        ClassLoader classLoader = getClass().getClassLoader();
        List<String> directories = Collections.singletonList("resource/bukkit/lang/");

        return new ClassLoaderLanguageLoader(classLoader, directories, logger);
    }
}
