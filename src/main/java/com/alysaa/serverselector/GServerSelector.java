package com.alysaa.serverselector;

import com.alysaa.serverselector.command.SelectorCommand;
import com.alysaa.serverselector.form.SelectorForm;
import com.alysaa.serverselector.listeners.CompassOnJoin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class GServerSelector extends JavaPlugin {
    private static GServerSelector plugin;

    @Override
    public void onEnable() {
        plugin = this;
        if (!loadConfig()) {
            getLogger().severe("Disabling due to configuration error.");
            return;
        }
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        SelectorForm.init();
        getCommand("servers").setExecutor(new SelectorCommand());
        Bukkit.getServer().getPluginManager().registerEvents(new CompassOnJoin(), this);
    }

    @Override
    public void onDisable() {
    }

    private boolean loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
            if (getConfig().contains("ConfigVersion") && (getConfig().getInt("ConfigVersion") == 1)) {
                return true;
            } else {
                getLogger().severe("Mismatched config version! Regenerate a new config.");
                return false;
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Plugin getInstance() {
        return plugin;
    }
}