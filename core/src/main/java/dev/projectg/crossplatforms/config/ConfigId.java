package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.interfacing.bedrock.FormConfig;
import dev.projectg.crossplatforms.interfacing.java.MenuConfig;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An enum containing the identities of all valid configuration files.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigId {

    public static final ConfigId GENERAL = ConfigId.builder()
        .file("config.yml")
        .version(GeneralConfig.VERSION)
        .minimumVersion(GeneralConfig.MINIMUM_VERSION)
        .clazz(GeneralConfig.class)
        .updater(GeneralConfig::updater)
        .postProcessor(config -> Logger.getLogger().setDebug(((GeneralConfig) config).isEnableDebug()))
        .build();

    public static final ConfigId BEDROCK_FORMS = ConfigId.builder()
        .file("bedrock-forms.yml")
        .version(FormConfig.VERSION)
        .minimumVersion(FormConfig.MINIMUM_VERSION)
        .clazz(FormConfig.class)
        .updater(FormConfig::updater)
        .build();

    public static final ConfigId JAVA_MENUS = ConfigId.builder()
        .file("java-menus.yml")
        .version(MenuConfig.VERSION)
        .clazz(MenuConfig.class)
        .build();

    /**
     * File name or file path relative to the parent directory that all configs registered to a {@link ConfigManager} share.
     */
    public final String file;

    public final int version;
    public final int minimumVersion;
    public final Class<? extends Configuration> clazz;

    @Nullable
    public final Supplier<ConfigurationTransformation.Versioned> updater;

    @Nullable
    public final Consumer<Configuration> postProcessor;

    public ConfigId(String file, int version, int minimumVersion, Class<? extends Configuration> clazz, @Nullable Supplier<ConfigurationTransformation.Versioned> updater) {
        this.file = file;
        this.version = version;
        this.minimumVersion = minimumVersion;
        this.clazz = clazz;
        this.updater = updater;
        this.postProcessor = null;
    }

    public ConfigId(String file, int version, Class<? extends Configuration> clazz) {
        this.file = file;
        this.version = version;
        this.minimumVersion = version;
        this.clazz = clazz;
        this.updater = null;
        this.postProcessor = null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String file;
        private int version = -1;
        private int minimumVersion = -1;
        private Class<? extends Configuration> clazz;

        @Nullable
        private Supplier<ConfigurationTransformation.Versioned> updater;

        @Nullable
        private Consumer<Configuration> postProcessor;

        private Builder() {

        }

        public Builder file(String file) {
            this.file = file;
            return this;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        public Builder minimumVersion(int minimumVersion) {
            this.minimumVersion = minimumVersion;
            return this;
        }

        public Builder clazz(Class<? extends Configuration> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder updater(Supplier<ConfigurationTransformation.Versioned> updater) {
            this.updater = updater;
            return this;
        }

        public Builder postProcessor(Consumer<Configuration> postProcessor) {
            this.postProcessor = postProcessor;
            return this;
        }

        public ConfigId build() {
            if (file == null) {
                throw new IllegalStateException("File cannot be null");
            } else if (version == -1) {
                throw new IllegalStateException("Minimum version may not be -1");
            } else if (minimumVersion == -1) {
                minimumVersion = version;
            } else if (clazz == null) {
                throw new IllegalStateException("Config class may not be null");
            }
            return new ConfigId(file, version, minimumVersion, clazz, updater, postProcessor);
        }
    }
}
