package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.interfacing.bedrock.FormConfig;
import dev.projectg.crossplatforms.interfacing.java.MenuConfig;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
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

    /**
     * File name or file path relative to the parent directory that all configs registered to a {@link ConfigManager} share.
     */
    public final String file;

    public final int version;
    public final int minimumVersion;
    public final Class<? extends Configuration> clazz;

    @Nullable
    public final Supplier<ConfigurationTransformation.Versioned> updater;

    public ConfigId(String file, int version, int minimumVersion, Class<? extends Configuration> clazz, @Nullable Supplier<ConfigurationTransformation.Versioned> updater) {
        this.file = file;
        this.version = version;
        this.minimumVersion = minimumVersion;
        this.clazz = clazz;
        this.updater = updater;
    }

    public ConfigId(String file, int version, Class<? extends Configuration> clazz) {
        this.file = file;
        this.version = version;
        this.minimumVersion = version;
        this.clazz = clazz;
        this.updater = null;
    }

    public static Set<ConfigId> defaults() {
        return new HashSet<>(DEFAULTS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigId configId = (ConfigId) o;
        return version == configId.version && minimumVersion == configId.minimumVersion && file.equals(configId.file) && clazz.equals(configId.clazz) && Objects.equals(updater, configId.updater);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, version, minimumVersion, clazz, updater);
    }
}
