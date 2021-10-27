package dev.projectg.geyserhub.config;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.config.updaters.SELECTOR_1;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ConfigManager {

    private static ConfigManager CONFIG_MANAGER;
    private static final Map<ConfigId, Map<Integer, Class<? extends ConfigUpdater>>> updaters = new HashMap<>();

    static {
        // todo: this is awful but oh well
        updaters.put(ConfigId.SELECTOR, Collections.singletonMap(1, SELECTOR_1.class));
    }

    private final Map<ConfigId, FileConfiguration> configurations = new HashMap<>();

    private final SelectorLogger logger;

    public ConfigManager() {
        if (CONFIG_MANAGER == null) {
            CONFIG_MANAGER = this;
        } else {
            throw new UnsupportedOperationException("Only one instance of ConfigManager is allowed!");
        }

        this.logger = SelectorLogger.getLogger();
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
        GeyserHubMain plugin = GeyserHubMain.getInstance();

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
            logger.warn("Mismatched config version in " + config.fileName + ". Expected version " + config.version + ", file is " + oldVersion);

            Map<Integer, Class<? extends ConfigUpdater>> relevantUpdaters = updaters.get(config);
            if (relevantUpdaters != null && !relevantUpdaters.isEmpty()) {
                logger.info("Attempting to automatically update " + config.fileName + ". Comments will be lost.");

                try {
                    String[] splitName = config.fileName.split("\\.");
                    configuration.save(new File(plugin.getDataFolder(), splitName[0] + "-old." + splitName[1]));
                } catch (IOException e) {
                    logger.severe("Failed to make a copy of " + config.fileName + " before attempting to automatically update it");
                    e.printStackTrace();
                    return false;
                }

                for (int version = oldVersion; version < config.version; version++) {
                    if (relevantUpdaters.containsKey(version)) {
                        logger.info("Updating " + config.fileName + " from version " + version + " to " + (version + 1));
                        try {
                            ConfigUpdater updater = ((ConfigUpdater) relevantUpdaters.get(version).getConstructors()[0].newInstance());

                            boolean fail;
                            try {
                                fail = !updater.update(configuration);
                            } catch (IllegalArgumentException e) {
                                fail = true;
                                e.printStackTrace();
                            }
                            if (fail) {
                                logger.severe("Failed to update " + config.fileName + " from version " + version + " to " + (version + 1));
                                break;
                            }
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            logger.severe("Failed to fetch a ConfigUpdater which should exist");
                            e.printStackTrace();
                            break;
                        }
                    }
                }

                try {
                    configuration.save(file);
                } catch (IOException e) {
                    logger.severe("Failed to save the updated version of " + config.fileName);
                    e.printStackTrace();
                }
            }

            if (configuration.getInt("Config-Version") == config.version) {
                logger.info(config.fileName + " was successfully updated to the latest version");
            } else {
                logger.warn("Failed to fully update " + config.fileName + " from version " + oldVersion + " to " + config.version);
                return false;
            }
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
    public static Map<String, Object> getMap(@Nonnull ConfigurationSection config) {
        Map<String, Object> map = new LinkedHashMap<>();

        for (String key : config.getKeys(false)) {
            Object value;
            if (config.isConfigurationSection(key)) {
                ConfigurationSection subSection = config.getConfigurationSection(key);
                Objects.requireNonNull(subSection);

                value = getMap(subSection);
            } else {
                value = config.get(key);
            }

            map.put(key, value);
        }

        return map;
    }
}
