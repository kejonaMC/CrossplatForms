package dev.projectg.geyserhub;

import dev.projectg.geyserhub.command.ReloadCommand;
import dev.projectg.geyserhub.command.SelectorCommand;
import dev.projectg.geyserhub.module.menu.BedrockMenu;
import dev.projectg.geyserhub.module.listeners.ItemInteract;
import dev.projectg.geyserhub.module.listeners.SelectorInventory;
import dev.projectg.geyserhub.module.listeners.ItemOnJoin;
import dev.projectg.geyserhub.module.message.Broadcast;
import dev.projectg.geyserhub.module.message.MessageJoin;
import dev.projectg.geyserhub.module.Placeholders;
import dev.projectg.geyserhub.module.scoreboard.ScoreboardManager;
import dev.projectg.geyserhub.module.world.WorldSettings;
import dev.projectg.geyserhub.utils.bstats.Metrics;
import dev.projectg.geyserhub.utils.bstats.SelectorLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GeyserHubMain extends JavaPlugin {
    private static GeyserHubMain plugin;
    private SelectorLogger logger;

    public static final int configVersion = 3;

    @Override
    public void onEnable() {
        plugin = this;
        new Metrics(this, 11427);
        logger = SelectorLogger.getLogger();
        if (!loadConfiguration()) {
            logger.severe("Disabling due to configuration error. Fix the formatting or regenerate a new one");
            return;
        }
        // Bungee channel for selector
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        new BedrockMenu();

        Objects.requireNonNull(getCommand("ghteleporter")).setExecutor(new SelectorCommand());
        Objects.requireNonNull(getCommand("ghreload")).setExecutor(new ReloadCommand());

        Bukkit.getServer().getPluginManager().registerEvents(new ItemInteract(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new SelectorInventory(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ItemOnJoin(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new WorldSettings(), this);

        if (getConfig().getBoolean("Scoreboard.Enable", false)) {
            initializeScoreboard();
        }
        if (getConfig().getBoolean("Enable-Join-Message", false)) {
            Bukkit.getServer().getPluginManager().registerEvents(new MessageJoin(), this);
        }
        Broadcast.startBroadcastTimer(getServer().getScheduler());
    }

    @Override
    public void onDisable() {
    }

    public boolean loadConfiguration() {
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
        // Get the config but don't actually load it into the main memory config
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
            if (!config.contains("Config-Version", true)) {
                logger.severe("Config-Version does not exist!");
                return false;
            } else if (!config.isInt("Config-Version")) {
                logger.severe("Config-Version is not an integer!");
                return false;
            } else if (!(config.getInt("Config-Version") == configVersion)) {
                logger.severe("Mismatched config version!");
                return false;
            } else {
                reloadConfig();
                return true;
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void initializeScoreboard() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try {
                ScoreboardManager.addScoreboard();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }, 20L, Placeholders.refreshRate * 20L);
    }

    public static GeyserHubMain getInstance() {
        return plugin;
    }

}