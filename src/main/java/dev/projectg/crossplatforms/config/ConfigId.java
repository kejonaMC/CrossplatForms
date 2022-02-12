package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.item.AccessItemConfig;
import dev.projectg.crossplatforms.interfacing.bedrock.FormConfig;
import dev.projectg.crossplatforms.interfacing.java.MenuConfig;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * An enum containing the identities of all valid configuration files.
 */
public enum ConfigId {
    GENERAL("config.yml", 1, GeneralConfig.class),
    ACCESS_ITEMS("access-items.yml", 2, AccessItemConfig.class, AccessItemConfig::updater),
    BEDROCK_FORMS("bedrock-forms.yml", 1, FormConfig.class),
    JAVA_MENUS("java-menus.yml", 1, MenuConfig.class);

    public static final ConfigId[] VALUES = values();

    public final String fileName;
    public final int version;
    public final Class<? extends Configuration> clazz;

    @Nullable
    public final Supplier<ConfigurationTransformation.Versioned> updater;

    ConfigId(String fileName, int version, Class<? extends Configuration> clazz, @Nullable Supplier<ConfigurationTransformation.Versioned> updater) {
        this.fileName = fileName;
        this.version = version;
        this.clazz = clazz;
        this.updater = updater;
    }

    ConfigId(String fileName, int version, Class<? extends Configuration> clazz) {
        this.fileName = fileName;
        this.version = version;
        this.clazz = clazz;
        this.updater = null;
    }
}
