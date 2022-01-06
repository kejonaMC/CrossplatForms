package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.form.AccessItems;
import dev.projectg.crossplatforms.form.bedrock.FormConfig;
import dev.projectg.crossplatforms.form.java.MenuConfig;
import lombok.RequiredArgsConstructor;

/**
 * An enum containing the identities of all valid configuration files.
 */
@RequiredArgsConstructor
public enum ConfigId {
    GENERAL("config.yml", GeneralConfig.class),
    ACCESS_ITEMS("access-items.yml", AccessItems.class),
    BEDROCK_FORMS("bedrock-forms.yml", FormConfig.class),
    JAVA_MENUS("java-menus.yml", MenuConfig.class);

    public static final ConfigId[] VALUES = values();

    public final String fileName;
    public final Class<? extends Configuration> clazz;
}
