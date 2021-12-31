package dev.projectg.geyserhub.config;

import dev.projectg.geyserhub.CrossplatForms;
import dev.projectg.geyserhub.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigManager {

    private static ConfigManager CONFIG_MANAGER;

    private final Map<ConfigId, FileConfiguration> configurations = new HashMap<>();

    private final Logger logger;

    public ConfigManager() {
        if (CONFIG_MANAGER == null) {
            CONFIG_MANAGER = this;
        } else {
            throw new UnsupportedOperationException("Only one instance of ConfigManager is allowed!");
        }

        this.logger = Logger.getLogger();
    }

    /**
     * Load every config in {@link ConfigId}
     * @return false if there was a failure loading any of configurations
     */
    public boolean loadAllConfigs() {
        boolean totalSuccess = true;
        for (ConfigId configId : ConfigId.VALUES) {
            if (!loadConfig(configId)) {
                totalSuccess = false;
                logger.severe("Configuration error in " + ConfigId.MAIN.fileName + " - Fix the issue or regenerate a new file.");
            }
        }
        return totalSuccess;
    }

    /**
     * Load a configuration from file.
     * @param config The configuration to load
     * @return The success state
     */
    public boolean loadConfig(@Nonnull ConfigId config) {
        CrossplatForms plugin = CrossplatForms.getInstance();

        // Get the file
        File file = new File(plugin.getDataFolder(), config.fileName);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                try {
                    if (!file.getParentFile().mkdirs()) {
                        logger.severe("Failed to create plugin folder!");
                        return false;
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            plugin.saveResource(config.fileName, false);
        }

        // Load the file into a config
        FileConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }

        // Check if it is the right version, attempt to update
        if (!configuration.contains("Config-Version", true)) {
            logger.severe("Config-Version does not exist in" + config.fileName + " !");
            return false;
        } else if (!configuration.isInt("Config-Version")) {
            logger.severe("Config-Version is not an integer in" + config.fileName + " !");
            return false;
        }

        int oldVersion = configuration.getInt("Config-Version");
        if (oldVersion != config.version) {
            logger.severe("Mismatched config version in " + config.fileName + ". Expected version " + config.version + ", file is " + oldVersion);
            return false;
        }


        // plugin.reloadConfig(); //todo is this line even necessary?
        this.configurations.put(config, configuration);
        logger.debug("Loaded configuration " + config.fileName + " successfully");
        return true;
    }

    /**
     * Get the given FileConfiguration in the stored map.
     * @param config the config ID
     * @return the FileConfiguration
     */
    public FileConfiguration getFileConfiguration(@Nonnull ConfigId config) {
        Objects.requireNonNull(config);
        return configurations.get(config);
    }

    /**
     * @return A the map of configuration names to FileConfigurations
     */
    public Map<ConfigId, FileConfiguration> getAllFileConfigurations() {
        return configurations;
    }

    /**
     * Converts a ConfigurationSection into a Map, whose keys and values are those of the ConfigurationSection.
     * Child ConfigurationSections will also be converted into Maps.
     */
    public static Map<String, Object> asMap(@Nonnull ConfigurationSection config) {
        Map<String, Object> map = new LinkedHashMap<>();

        for (String key : config.getKeys(false)) {
            Object value;
            if (config.isConfigurationSection(key)) {
                ConfigurationSection subSection = config.getConfigurationSection(key);
                Objects.requireNonNull(subSection);

                value = asMap(subSection);
            } else {
                value = config.get(key);
            }

            map.put(key, value);
        }

        return map;
    }
}
