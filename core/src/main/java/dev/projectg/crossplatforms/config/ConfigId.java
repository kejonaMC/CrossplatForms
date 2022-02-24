package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.interfacing.bedrock.FormConfig;
import dev.projectg.crossplatforms.interfacing.java.MenuConfig;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * An enum containing the identities of all valid configuration files.
 */
public class ConfigId {
    public static final ConfigId GENERAL = new ConfigId("config.yml", GeneralConfig.VERSION, GeneralConfig.class);
    public static final ConfigId BEDROCK_FORMS = new ConfigId("bedrock-forms.yml", FormConfig.VERSION, FormConfig.MINIMUM_VERSION, FormConfig.class, FormConfig::updater);
    public static final ConfigId JAVA_MENUS = new ConfigId("java-menus.yml", MenuConfig.VERSION, MenuConfig.class);

    private static final Set<ConfigId> DEFAULTS = new HashSet<>();

    static {
        DEFAULTS.add(GENERAL);
        DEFAULTS.add(BEDROCK_FORMS);
        DEFAULTS.add(JAVA_MENUS);
    }

    public final String fileName;
    public final int version;
    public final int minimumVersion;
    public final Class<? extends Configuration> clazz;

    @Nullable
    public final Supplier<ConfigurationTransformation.Versioned> updater;

    public ConfigId(String fileName, int version, int minimumVersion, Class<? extends Configuration> clazz, @Nullable Supplier<ConfigurationTransformation.Versioned> updater) {
        this.fileName = fileName;
        this.version = version;
        this.minimumVersion = minimumVersion;
        this.clazz = clazz;
        this.updater = updater;
    }

    public ConfigId(String fileName, int version, Class<? extends Configuration> clazz) {
        this.fileName = fileName;
        this.version = version;
        this.minimumVersion = version;
        this.clazz = clazz;
        this.updater = null;
    }

    public static Set<ConfigId> defaults() {
        return new HashSet<>(DEFAULTS);
    }
}
