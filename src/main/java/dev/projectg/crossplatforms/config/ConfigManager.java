package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.ComponentSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.FormImageSerializer;
import dev.projectg.crossplatforms.utils.FileUtils;
import org.geysermc.cumulus.util.FormImage;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigManager {

    private final YamlConfigurationLoader.Builder loaderBuilder;
    private final Map<Class<? extends Configuration>, Configuration> configurations = new HashMap<>();
    private final File directory;
    private final Logger logger;

    public ConfigManager(File directory, Logger logger) {
        this.directory = directory;
        this.logger = logger;

        loaderBuilder = YamlConfigurationLoader.builder();
        loaderBuilder.defaultOptions(opts -> (opts.serializers(builder -> {
            builder.registerExact(BedrockForm.class, new BedrockFormSerializer());
            builder.registerExact(FormImage.class, new FormImageSerializer());
            builder.registerExact(CustomComponent.class, new ComponentSerializer());
        })));
        loaderBuilder.defaultOptions(opts -> opts.implicitInitialization(false));
    }

    /**
     * Load every config in {@link ConfigId}
     * @return false if there was a failure loading any of configurations
     */

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean loadAllConfigs() {
        for (ConfigId configId : ConfigId.VALUES) {
            try {
                if (!loadConfig(configId)) {
                    logger.severe("Configuration error in " + configId.fileName + " - Fix the issue or regenerate a new file.");
                    return false;
                }
            } catch (IOException e) {
                logger.severe("Failed to load configuration " + configId.fileName);
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Load a configuration from file. The config will only be loaded into memory if there were zero errors.
     * @param config The configuration to load
     * @return The success state
     */
    private boolean loadConfig(ConfigId config) throws IOException {
        File file = FileUtils.fileOrCopiedFromResource(new File(directory, config.fileName));
        YamlConfigurationLoader loader = loaderBuilder.file(file).build();
        Configuration mapped = loader.load().get(config.clazz);

        // todo: config translation for different config-versions
        boolean fail = false;

        if (mapped == null) {
            logger.severe("Failed to deserialize " + config.fileName + " to " + config.clazz + ": Mapped object returned null.");
            fail = true;
        } else if (mapped.getVersion() != mapped.getDefaultVersion()) {
            logger.severe(config.fileName + " is outdated. Please back it up and regenerate a new config");
            fail = true;
        }

        if (fail) {
            try {
                // Get the default values so that the plugin can be reloaded at a later time
                configurations.put(config.clazz, config.clazz.getConstructor().newInstance());
                logger.warn("Falling back to minimal defaults for configuration: " + config.fileName);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                logger.severe("Failed to fallback to defaults for configuration " + config.fileName + ": " + e.getLocalizedMessage());
                if (logger.isDebug()) {
                    e.printStackTrace();
                }
                return false;
            }
        } else {
            configurations.put(config.clazz, mapped);
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public <T extends Configuration> Optional<T> getConfig(Class<T> clazz) {
        return Optional.ofNullable((T) configurations.get(clazz));
    }
}
