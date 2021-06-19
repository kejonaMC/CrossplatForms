package dev.projectg.geyserhub;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigManager {

    private static ConfigManager CONFIG_MANAGER;

    private final Map<ConfigId, FileConfiguration> configurations = new HashMap<>();

    public ConfigManager() {
        if (CONFIG_MANAGER == null) {
            CONFIG_MANAGER = this;
        } else {
            throw new UnsupportedOperationException("Only one instance of ConfigManager is allowed!");
        }
    }

    /**
     * Load a configuration from file.
     * @param config The configuration to load
     * @return The success state
     */
    public boolean loadConfiguration(@Nonnull ConfigId config) {
        Objects.requireNonNull(config);

        GeyserHubMain plugin = GeyserHubMain.getInstance();
        SelectorLogger logger = SelectorLogger.getLogger();

        File file = new File(plugin.getDataFolder(), config.fileName);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
            } catch (SecurityException e) {
                e.printStackTrace();
                return false;
            }
            plugin.saveResource(config.fileName, false);
        }
        FileConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        if (!configuration.contains("Config-Version", true)) {
            logger.severe("Config-Version does not exist in" + config.fileName + " !");
            return false;
        } else if (!configuration.isInt("Config-Version")) {
            logger.severe("Config-Version is not an integer in" + config.fileName + " !");
            return false;
        //} else if (configuration.getInt("Config-Version") != Objects.requireNonNull(configuration.getDefaults()).getInt("Config-Version")) {
            //logger.severe("Mismatched config version in " + configFileName + " ! Generate a new config and migrate your settings!");
            //return false;
        } else {
            plugin.reloadConfig();
            this.configurations.put(config, configuration);
            logger.debug("Loaded configuration successfully");
            return true;
        }
    }

    /**
     * Get the given FileConfiguration in the stored map.
     * @param config the config ID
     * @return the FileConfiguration
     */
    @Nullable
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
     * An enum containing the identities of all valid configuration files.
     */
    public enum ConfigId {
        MAIN("config.yml"),
        SELECTOR("selector.yml");

        public static final ConfigId[] VALUES = values();

        public final String fileName;

        ConfigId(String fileName){
            this.fileName = fileName;
        }
    }
}
