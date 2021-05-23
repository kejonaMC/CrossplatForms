package dev.projectg.geyserhub;

import dev.projectg.geyserhub.command.ReloadCommand;
import dev.projectg.geyserhub.command.SelectorCommand;
import dev.projectg.geyserhub.bedrockmenu.BedrockMenu;
import dev.projectg.geyserhub.listeners.ItemInteract;
import dev.projectg.geyserhub.listeners.ItemInventory;
import dev.projectg.geyserhub.listeners.ItemJoin;
import dev.projectg.geyserhub.listeners.MessageJoin;
import dev.projectg.geyserhub.scoreboard.Placeholders;
import dev.projectg.geyserhub.scoreboard.ScoreboardManager;
import dev.projectg.geyserhub.utils.bstats.Metrics;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GeyserHubMain extends JavaPlugin {
    private static GeyserHubMain plugin;
    private SelectorLogger logger;

    @Override
    public void onEnable() {
        plugin = this;
        new Metrics(this, 11427);
        logger = SelectorLogger.getLogger();
        if (!loadConfiguration()) {
            logger.severe("Disabling due to configuration error.");
            return;
        }
        // Bungee channel for selector
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        new BedrockMenu(getConfig());

        Objects.requireNonNull(getCommand("ghteleporter")).setExecutor(new SelectorCommand());
        Objects.requireNonNull(getCommand("ghreload")).setExecutor(new ReloadCommand());

        Bukkit.getServer().getPluginManager().registerEvents(new ItemInteract(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ItemInventory(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ItemJoin(), this);

        if (getConfig().getBoolean("Scoreboard.Enable", false)) {
            enableScorboards();
        }
        if (getConfig().getBoolean("Enable-Join-Message", false)) {
            Bukkit.getServer().getPluginManager().registerEvents(new MessageJoin(), this);
        }
    }

    @Override
    public void onDisable() {
    }

    private void enableScorboards() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            Placeholders.vault = 0;
        } else {
            this.setupPermissions();
        }
        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Placeholders.PAPI = 0;
        }
        if (this.getServer().getPluginManager().getPlugin("Essentials") == null) {
            Placeholders.essentials = 0;
        } else {
            this.setupEconomy();
        }
        this.Scheduler();
        Bukkit.getServer().getPluginManager().registerEvents(new ScoreboardManager(), this);
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
            if (config.contains("Config-Version", true) && (config.getInt("Config-Version") == 3)) {
                // Load the config into the main memory config
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
    public void Scheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try {
                ScoreboardManager.addScoreboard();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }, 20L, Placeholders.isb * 20L);
    }

    // todo: I think we can just set vault as a softdepend instead of doing this?

    private void setupEconomy() {
        RegisteredServiceProvider economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            Placeholders.economy = (Economy) economyProvider.getProvider();
        }
    }

    private void setupPermissions() {
        RegisteredServiceProvider permissionProvider = this.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            Placeholders.permission = (Permission) permissionProvider.getProvider();
        }
    }

    public static GeyserHubMain getInstance() {
        return plugin;
    }
}