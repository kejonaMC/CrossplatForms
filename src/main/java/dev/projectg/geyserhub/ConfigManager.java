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

    private static final int DEFAULT_VERSION = 4;
    private static final int SELECTOR_VERSION = 1;

    private final Map<String, FileConfiguration> configurations = new HashMap<>();

    public ConfigManager() {
        if (CONFIG_MANAGER == null) {
            CONFIG_MANAGER = this;
        } else {
            throw new UnsupportedOperationException("Only one instance of ConfigManager is allowed!");
        }
    }

    // todo: better code

    public boolean loadDefaultConfiguration() {
        SelectorLogger logger = SelectorLogger.getLogger();

        File configFile = new File(GeyserHubMain.getInstance().getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
            } catch (SecurityException e) {
                e.printStackTrace();
                return false;
            }
            GeyserHubMain.getInstance().saveResource("config.yml", false);
        }
        // Get the config but don't actually load it into the main memory config
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        try {
            config.load(configFile);
            if (!config.contains("Config-Version", true)) {
                logger.severe("Config-Version does not exist!");
                return false;
            } else if (!config.isInt("Config-Version")) {
                logger.severe("Config-Version is not an integer!");
                return false;
            } else if (config.getInt("Config-Version") != DEFAULT_VERSION) {
                logger.severe("Mismatched config version! Generate a new config and migrate your settings!");
                return false;
            } else {
                GeyserHubMain.getInstance().reloadConfig();
                logger.debug("Loaded configuration successfully");
                return true;
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean loadSelectorConfiguration() {
        SelectorLogger logger = SelectorLogger.getLogger();

        File file = new File(GeyserHubMain.getInstance().getDataFolder(), "selector.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
            } catch (SecurityException e) {
                e.printStackTrace();
                return false;
            }
            GeyserHubMain.getInstance().saveResource("selector.yml", false);
        }
        FileConfiguration selectorConfig = new YamlConfiguration();
        try {
            selectorConfig.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        if (!selectorConfig.contains("Config-Version", true)) {
            logger.severe("Config-Version does not exist!");
            return false;
        } else if (!selectorConfig.isInt("Config-Version")) {
            logger.severe("Config-Version is not an integer!");
            return false;
        } else if (selectorConfig.getInt("Config-Version") != SELECTOR_VERSION) {
            logger.severe("Mismatched config version! Generate a new config and migrate your settings!");
            return false;
        } else {
            GeyserHubMain.getInstance().reloadConfig();
            this.configurations.put("selector", selectorConfig);
            logger.debug("Loaded configuration successfully");
            return true;
        }
    }

    @Nullable
    public FileConfiguration getFileConfiguration(@Nonnull String configName) {
        Objects.requireNonNull(configName);
        return configurations.get(configName);
    }
}
