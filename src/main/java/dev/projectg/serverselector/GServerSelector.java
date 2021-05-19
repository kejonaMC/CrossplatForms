package dev.projectg.serverselector;

import dev.projectg.serverselector.command.ReloadCommand;
import dev.projectg.serverselector.command.SelectorCommand;
import dev.projectg.serverselector.form.SelectorForm;
import dev.projectg.serverselector.listeners.SelectorItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class GServerSelector extends JavaPlugin {
    private static GServerSelector plugin;
    private SelectorLogger logger;

    @Override
    public void onEnable() {
        plugin = this;
        logger = SelectorLogger.getLogger();
        if (!loadConfig()) {
            logger.severe("Disabling due to configuration error.");
            return;
        }
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        SelectorForm.init();
        getCommand("gteleporter").setExecutor(new SelectorCommand());
        getCommand("gssreload").setExecutor(new ReloadCommand());
        Bukkit.getServer().getPluginManager().registerEvents(new SelectorItem(), this);
    }

    @Override
    public void onDisable() {
    }

    public boolean loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
            } catch (SecurityException e) {
                e.printStackTrace();
                return false;
            }
            saveResource("config.yml", false);
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
            if (config.contains("ConfigVersion") && (config.getInt("ConfigVersion") == 2)) {
                reloadConfig();
                return true;
            } else {
                logger.severe("Mismatched config version! Regenerate a new config.");
                return false;
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static GServerSelector getInstance() {
        return plugin;
    }
}