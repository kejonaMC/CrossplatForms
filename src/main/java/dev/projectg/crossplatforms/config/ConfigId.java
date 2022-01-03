package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.config.mapping.AccessItems;
import dev.projectg.crossplatforms.config.mapping.Configuration;
import dev.projectg.crossplatforms.config.mapping.GeneralConfig;
import dev.projectg.crossplatforms.config.mapping.bedrock.FormConfig;
import dev.projectg.crossplatforms.config.mapping.java.MenuConfig;
import lombok.RequiredArgsConstructor;

/**
 * An enum containing the identities of all valid configuration files.
 */
@RequiredArgsConstructor
public enum ConfigId {
    GENERAL("config.yml", GeneralConfig.class, true),
    ACCESS_ITEMS("access-items.yml", AccessItems.class, false),
    BEDROCK_FORMS("bedrock-forms.yml", FormConfig.class, false),
    JAVA_MENUS("java-menus.yml", MenuConfig.class, false);

    public static final ConfigId[] VALUES = values();

    public final String fileName;
    public final Class<? extends Configuration> clazz;
    public final boolean critical;
}
