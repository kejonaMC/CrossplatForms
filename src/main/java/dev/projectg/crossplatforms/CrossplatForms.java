package dev.projectg.crossplatforms;

import dev.projectg.crossplatforms.command.MainCommand;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.config.GeneralConfig;
import dev.projectg.crossplatforms.form.AccessItemRegistry;
import dev.projectg.crossplatforms.form.InventoryManager;
import dev.projectg.crossplatforms.form.java.JavaMenuListeners;
import dev.projectg.crossplatforms.form.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.form.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FloodgateHandler;
import dev.projectg.crossplatforms.handler.GeyserHandler;
import dev.projectg.crossplatforms.utils.FileUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.session.auth.AuthType;

import java.util.Objects;
import java.util.Properties;

@Getter
public class CrossplatForms extends JavaPlugin {
    @Getter
    private static CrossplatForms instance;

    private String branch = "unknown";
    private String commit = "unknown";
    private ConfigManager configManager;
    private BedrockHandler bedrockHandler;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        Logger logger = Logger.getLogger();

        try {
            Properties gitProperties = new Properties();
            gitProperties.load(FileUtils.getResource("git.properties"));
            branch = gitProperties.getProperty("git.branch", "unknown");
            commit = gitProperties.getProperty("git.commit.id.abbrev", "unknown");
            logger.info("Branch: " + branch + ", Commit: " + commit);
        } catch (Exception e) {
            logger.warn("Unable to load resource: git.properties");
            e.printStackTrace();
        }

        PluginManager pluginManager = Bukkit.getPluginManager();

        if (pluginManager.isPluginEnabled("floodgate")) {
            if (pluginManager.isPluginEnabled("Geyser-Spigot") && GeyserImpl.getInstance().getConfig().getRemote().getAuthType() != AuthType.FLOODGATE ) {
                logger.warn("Floodgate is installed but auth-type in Geyser's config is not set to Floodgate! Ignoring Floodgate.");
                bedrockHandler = new GeyserHandler();
            } else {
                bedrockHandler = new FloodgateHandler();
            }
        } else if (pluginManager.isPluginEnabled("Geyser-Spigot")) {
            bedrockHandler = new GeyserHandler();
            logger.warn("Floodgate is recommended and more stable!");
        } else {
            logger.severe("Geyser or Floodgate is required! Disabling.");
            return;
        }

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            logger.warn("This plugin works best with PlaceholderAPI! Since you don't have it installed, only %player_name% and %player_uuid% will work in the GeyserHub config!");
        }

        configManager = new ConfigManager(getDataFolder(), logger);
        if (!configManager.loadAllConfigs()) {
            logger.severe("Disabling due to configuration error.");
            return;
        }
        logger.setDebug(configManager.getConfig(GeneralConfig.class).isEnableDebug());

        // Bungee channel for selector
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Load forms
        AccessItemRegistry accessItemRegistry = new AccessItemRegistry(this);
        BedrockFormRegistry bedrockFormRegistry = new BedrockFormRegistry();
        JavaMenuRegistry javaMenuRegistry = new JavaMenuRegistry();

        Objects.requireNonNull(getCommand("forms")).setExecutor(new MainCommand(bedrockHandler, bedrockFormRegistry, javaMenuRegistry));

        // Listeners for the Bedrock and Java menus
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryManager(accessItemRegistry, bedrockFormRegistry, javaMenuRegistry), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JavaMenuListeners(javaMenuRegistry), this);

        logger.info("Took " + (System.currentTimeMillis() - start) + "ms to boot CrossplatForms.");
    }
}
