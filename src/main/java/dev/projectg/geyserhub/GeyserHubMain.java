package dev.projectg.geyserhub;

import dev.projectg.geyserhub.command.GeyserHubCommand;
import dev.projectg.geyserhub.config.ConfigManager;
import dev.projectg.geyserhub.module.menu.AccessItemRegistry;
import dev.projectg.geyserhub.module.menu.CommonMenuListeners;
import dev.projectg.geyserhub.module.menu.java.JavaMenuListeners;
import dev.projectg.geyserhub.module.menu.bedrock.BedrockFormRegistry;
import dev.projectg.geyserhub.module.menu.java.JavaMenuRegistry;
import dev.projectg.geyserhub.module.message.Broadcast;
import dev.projectg.geyserhub.module.message.MessageJoin;
import dev.projectg.geyserhub.module.scoreboard.ScoreboardManager;
import dev.projectg.geyserhub.module.teleporter.JoinTeleporter;
import dev.projectg.geyserhub.module.world.WorldSettings;
import dev.projectg.geyserhub.utils.FileUtils;
import dev.projectg.geyserhub.utils.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class GeyserHubMain extends JavaPlugin {
    private static GeyserHubMain plugin;

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        plugin = this;
        new Metrics(this, 11427);
        // getting the logger forces the config to load before our loadConfiguration() is called...
        SelectorLogger logger = SelectorLogger.getLogger();

        try {
            Properties gitProperties = new Properties();
            gitProperties.load(FileUtils.getResource("git.properties"));
            logger.info("Branch: " + gitProperties.getProperty("git.branch", "Unknown") + ", Commit: " + gitProperties.getProperty("git.commit.id.abbrev", "Unknown"));
        } catch (IOException | NullPointerException | IllegalArgumentException e) {
            logger.warn("Unable to load resource: git.properties");
            e.printStackTrace();
        }

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            logger.warn("This plugin works best with PlaceholderAPI! Since you don't have it installed, only %player_name% and %player_uuid% will work in the GeyserHub config!");
        }

        configManager = new ConfigManager();
        if (!configManager.loadAllConfigs()) {
            logger.severe("Disabling due to configuration error.");
            return;
        }

        // Bungee channel for selector
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Load forms
        AccessItemRegistry accessItemRegistry = new AccessItemRegistry();
        BedrockFormRegistry bedrockFormRegistry = new BedrockFormRegistry();
        JavaMenuRegistry javaMenuRegistry = new JavaMenuRegistry();

        // todo: and add command suggestions/completions, help pages that only shows available commands
        Objects.requireNonNull(getCommand("ghub")).setExecutor(new GeyserHubCommand(bedrockFormRegistry, javaMenuRegistry));

        // todo: sort all of this, and make checking for enable value in config consistent

        // Listeners for the Bedrock and Java menus
        Bukkit.getServer().getPluginManager().registerEvents(new CommonMenuListeners(accessItemRegistry, bedrockFormRegistry, javaMenuRegistry), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JavaMenuListeners(javaMenuRegistry), this);

        // Listener the Join Teleporter module
        Bukkit.getServer().getPluginManager().registerEvents(new JoinTeleporter(), this);

        // Listener for world settings
        Bukkit.getServer().getPluginManager().registerEvents(new WorldSettings(), this);

        // load the scoreboard if enabled
        if (getConfig().getBoolean("Scoreboard.Enable", false)) {
            initializeScoreboard();
        }

        // Enable the join message if enabled
        if (getConfig().getBoolean("Enable-Join-Message", false)) {
            Bukkit.getServer().getPluginManager().registerEvents(new MessageJoin(), this);
        }

        // The random interval broadcast module
        Broadcast.startBroadcastTimer(getServer().getScheduler());

        logger.info("Took " + (System.currentTimeMillis() - start) + "ms to boot GeyserHub.");
    }

    public void initializeScoreboard() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try {
                ScoreboardManager.addScoreboard();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }, 20L, ScoreboardManager.REFRESH_RATE * 20L);
    }

    public static GeyserHubMain getInstance() {
        return plugin;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
