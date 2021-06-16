package dev.projectg.geyserhub.utils;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static dev.projectg.geyserhub.GeyserHubMain.configVersion;
import static dev.projectg.geyserhub.GeyserHubMain.selectorConfigVersion;

public class ConfigManager {

    private static FileConfiguration selectorConfig;

    public static boolean loadDefaultConfiguration() {
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
            if (!config.contains("Config-Version", true)) {
                SelectorLogger.severe("Config-Version does not exist!");
                return false;
            } else if (!config.isInt("Config-Version")) {
                SelectorLogger.severe("Config-Version is not an integer!");
                return false;
            } else if (!(config.getInt("Config-Version") == configVersion)) {
                SelectorLogger.severe("Mismatched config version! Generate a new config and migrate your settings!");
                return false;
            } else {
                GeyserHubMain.getInstance().reloadConfig();
                SelectorLogger.debug("Loaded configuration successfully");
                return true;
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean loadSelectorConfiguration() {
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
        // Get the config but don't actually load it into the main memory config
        FileConfiguration selectorConfig = new YamlConfiguration();
        ConfigManager.selectorConfig = YamlConfiguration.loadConfiguration(file);
        if (!selectorConfig.contains("Config-Version", true)) {
            SelectorLogger.severe("Config-Version does not exist!");
            return false;
        } else if (!selectorConfig.isInt("Config-Version")) {
            SelectorLogger.severe("Config-Version is not an integer!");
            return false;
        } else if (!(selectorConfig.getInt("Config-Version") == selectorConfigVersion)) {
            SelectorLogger.severe("Mismatched config version! Generate a new config and migrate your settings!");
            return false;
        } else {
            GeyserHubMain.getInstance().reloadConfig();
            SelectorLogger.debug("Loaded configuration successfully");
            return true;
        }
    }
    public static FileConfiguration get(){
        return selectorConfig;
    }
}
