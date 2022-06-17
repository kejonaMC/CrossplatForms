package dev.kejona.crossplatforms.spigot.common;

import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import dev.kejona.crossplatforms.Constants;
import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.CrossplatFormsBootstrap;
import dev.kejona.crossplatforms.JavaUtilLogger;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.accessitem.AccessItemConfig;
import dev.kejona.crossplatforms.accessitem.GiveCommand;
import dev.kejona.crossplatforms.accessitem.InspectItemCommand;
import dev.kejona.crossplatforms.action.ActionSerializer;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.config.ConfigId;
import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.handler.BasicPlaceholders;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.spigot.common.handler.PlaceholderAPIHandler;
import dev.kejona.crossplatforms.spigot.common.handler.SpigotCommandOrigin;
import dev.kejona.crossplatforms.spigot.common.handler.SpigotHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.CustomChart;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public abstract class SpigotBase extends JavaPlugin implements CrossplatFormsBootstrap {
    
    protected static final String PIE_CHART_LEGACY = "legacy";
    protected static final int METRICS_ID = 14707;

    public static final LegacyComponentSerializer LEGACY_SERIALIZER = BukkitComponentSerializer.legacy();
    private static SpigotBase INSTANCE;

    static {
        // load information from build.properties
        Constants.fetch();
    }

    private Logger logger;
    protected Server server;
    protected BukkitAudiences audiences;
    private Metrics metrics;

    protected SpigotBase() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        logger = new JavaUtilLogger(getLogger());
        metrics = new Metrics(this, METRICS_ID);
        server = getServer();
        audiences = BukkitAudiences.create(this);
        ServerHandler serverHandler = new SpigotHandler(this, audiences);

        convertGeyserHubConfig();

        // Yes, this is not Paper-exclusive plugin. Cloud handles this gracefully.
        PaperCommandManager<CommandOrigin> commandManager;
        try {
            commandManager = new PaperCommandManager<>(
                this,
                CommandExecutionCoordinator.simpleCoordinator(),
                (SpigotCommandOrigin::new),
                origin -> (CommandSender) origin.getHandle()
            );
        } catch (Exception e) {
            logger.severe("Failed to create CommandManager, stopping");
            e.printStackTrace();
            return;
        }

        if (attemptBrigadier()) {
            try {
                // Brigadier is ideal if possible. Allows for much more readable command options, especially on BE.
                commandManager.registerBrigadier();
            } catch (BukkitCommandManager.BrigadierFailureException e) {
                logger.warn("Failed to initialize Brigadier support: " + e.getMessage());
            }
        }

        // For ServerAction
        server.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Placeholders placeholders;
        if (server.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholders = new PlaceholderAPIHandler(this);
        } else {
            logger.warn("This plugin works best with PlaceholderAPI! Since you don't have it installed, only %player_name% and %player_uuid% will work (typically).");
            placeholders = new BasicPlaceholders();
        }

        CrossplatForms crossplatForms = new CrossplatForms(
            logger,
            getDataFolder().toPath(),
            serverHandler,
            "forms",
            commandManager,
            placeholders,
            this
        );

        if (!crossplatForms.isSuccess()) {
            return;
        }

        SpigotAccessItemsBase accessItems = accessItems(crossplatForms);
        server.getPluginManager().registerEvents(accessItems, this);

        // Commands added by access items
        new GiveCommand(crossplatForms, accessItems).register(commandManager, crossplatForms.getCommandBuilder());
        new InspectItemCommand(crossplatForms, accessItems).register(commandManager, crossplatForms.getCommandBuilder());
    }

    @Override
    public void preConfigLoad(ConfigManager configManager) {
        configManager.register(ConfigId.JAVA_MENUS);
        configManager.register(AccessItemConfig.asConfigId());

        ActionSerializer actionSerializer = configManager.getActionSerializer();
        actionSerializer.simpleGenericAction(ServerAction.TYPE, String.class, ServerAction.class);
        actionSerializer.simpleMenuAction(CloseMenuAction.TYPE, String.class, CloseMenuAction.class);
    }

    @Override
    public Interfacer interfaceManager() {
        SpigotInterfacer manager = new SpigotInterfacer();
        server.getPluginManager().registerEvents(manager, this);
        return manager;
    }

    @Override
    public void onDisable() {
        if (audiences != null) {
            audiences.close();
        }
        server.getMessenger().unregisterOutgoingPluginChannel(this);
    }

    @Override
    public void addCustomChart(CustomChart chart) {
        metrics.addCustomChart(chart);
    }

    private void convertGeyserHubConfig() {
        File selector = new File(getDataFolder(), "selector.yml");
        if (selector.exists()) {
            try {
                GeyserHubConverter.convert(selector);
            } catch (IOException e) {
                logger.warn("Failed to convert " + selector.getName() + ":");
                if (logger.isDebug()) {
                    e.printStackTrace();
                } else {
                    logger.warn(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        }
    }
    
    public abstract boolean attemptBrigadier();
    
    public abstract SpigotAccessItemsBase accessItems(CrossplatForms crossplatForms);

    public static SpigotBase getInstance() {
        return INSTANCE;
    }
}
