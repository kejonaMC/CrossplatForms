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

    private final Map<String, FileConfiguration> configurations = new HashMap<>();

    public ConfigManager() {
        if (CONFIG_MANAGER == null) {
            CONFIG_MANAGER = this;
        } else {
            throw new UnsupportedOperationException("Only one instance of ConfigManager is allowed!");
        }
    }

    public boolean loadConfiguration(@Nonnull String configName) {
        Objects.requireNonNull(configName);

        GeyserHubMain plugin = GeyserHubMain.getInstance();
        SelectorLogger logger = SelectorLogger.getLogger();

        String configFileName = configName + ".yml";
        File file = new File(plugin.getDataFolder(), configFileName);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
            } catch (SecurityException e) {
                e.printStackTrace();
                return false;
            }
            plugin.saveResource(configFileName, false);
        }
        FileConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        if (!configuration.contains("Config-Version", true)) {
            logger.severe("Config-Version does not exist in" + configFileName + " !");
            return false;
        } else if (!configuration.isInt("Config-Version")) {
            logger.severe("Config-Version is not an integer in" + configFileName + " !");
            return false;
        } else if (configuration.getInt("Config-Version") != Objects.requireNonNull(configuration.getDefaults()).getInt("Config-Version")) {
            logger.severe("Mismatched config version in " + configFileName + " ! Generate a new config and migrate your settings!");
            return false;
        } else {
            plugin.reloadConfig();
            this.configurations.put(configName, configuration);
            logger.debug("Loaded configuration successfully");
            return true;
        }
    }

    /**
     * Get the given FileConfiguration in the stored map.
     * @param configName the name of the config file, without ".yml"
     * @return the FileConfiguration
     */
    @Nullable
    public FileConfiguration getFileConfiguration(@Nonnull String configName) {
        Objects.requireNonNull(configName);
        return configurations.get(configName);
    }

    /**
     * @return A the map of configuration names to FileConfigurations
     */
    public Map<String, FileConfiguration> getAllFileConfigurations() {
        return configurations;
    }
}
