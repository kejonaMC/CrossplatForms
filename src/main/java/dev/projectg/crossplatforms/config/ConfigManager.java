package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.config.mapping.Configuration;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final Map<Class<? extends Configuration>, Configuration> configurations = new HashMap<>();
    private final Logger logger;

    public ConfigManager(Logger logger) {
        this.logger = logger;
    }

    /**
     * Load every config in {@link ConfigId}
     * @return false if there was a failure loading any of configurations
     */
    public boolean loadAllConfigs() {
        boolean totalSuccess = true;
        for (ConfigId configId : ConfigId.VALUES) {
            try {
                if (!loadConfig(configId)) {
                    totalSuccess = false;
                    logger.severe("Configuration error in " + configId.fileName + " - Fix the issue or regenerate a new file.");
                }
            } catch (ConfigurateException e) {
                totalSuccess = false;
                logger.severe("Configuration error in " + configId.fileName + " - Fix the issue or regenerate a new file.");
            }
        }
        return totalSuccess;
    }

    /**
     * Load a configuration from file. The config will only be loaded into memory if there were zero errors.
     * @param config The configuration to load
     * @return The success state
     */
    private boolean loadConfig(ConfigId config) throws ConfigurateException {
        File file = new File(config.fileName);
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().file(file).build();
        Configuration mapped = loader.load().get(config.clazz);

        if (mapped == null) {
            logger.severe("Failed to deserialize " + config.fileName + " to " + config.clazz + ": Mapped object returned null.");
            return false;
        }
        if (mapped.getVersion() != mapped.getDefaultVersion()) {
            logger.severe(config.fileName + " is outdated. Please back it up and regenerate a new config");
            return false;
        }
        configurations.put(config.clazz, mapped);
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T extends Configuration> T getConfig(Class<T> clazz) {
        return (T) configurations.get(clazz);
    }
}
