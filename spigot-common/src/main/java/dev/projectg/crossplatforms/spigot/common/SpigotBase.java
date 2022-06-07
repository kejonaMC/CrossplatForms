package dev.projectg.crossplatforms.spigot.common;

import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.CrossplatFormsBootstrap;
import dev.projectg.crossplatforms.JavaUtilLogger;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.accessitem.AccessItemConfig;
import dev.projectg.crossplatforms.accessitem.GiveCommand;
import dev.projectg.crossplatforms.accessitem.InspectItemCommand;
import dev.projectg.crossplatforms.action.ActionSerializer;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.handler.BasicPlaceholders;
import dev.projectg.crossplatforms.handler.Placeholders;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.Interfacer;
import dev.projectg.crossplatforms.spigot.common.handler.PlaceholderAPIHandler;
import dev.projectg.crossplatforms.spigot.common.handler.SpigotCommandOrigin;
import dev.projectg.crossplatforms.spigot.common.handler.SpigotHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.CustomChart;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SpigotBase extends JavaPlugin implements CrossplatFormsBootstrap {
    
    protected static final String PIE_CHART_LEGACY = "legacy";
    protected static final int METRICS_ID = 14707;

    public static final LegacyComponentSerializer LEGACY_SERIALIZER = BukkitComponentSerializer.legacy();
    private static SpigotBase INSTANCE;

    static {
        // load information from build.properties
        Constants.fetch();
    }

    private CrossplatForms crossplatForms;
    protected Server server;
    protected BukkitAudiences audiences;
    private Metrics metrics;

    protected SpigotBase() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        Logger logger = new JavaUtilLogger(getLogger());
        if (crossplatForms != null) {
            logger.warn("Bukkit reloading is NOT supported!");
        }
        metrics = new Metrics(this, METRICS_ID);
        server = getServer();
        audiences = BukkitAudiences.create(this);
        ServerHandler serverHandler = new SpigotHandler(this, audiences);

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

        crossplatForms = new CrossplatForms(
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
    
    public abstract boolean attemptBrigadier();
    
    public abstract SpigotAccessItemsBase accessItems(CrossplatForms crossplatForms);

    public static SpigotBase getInstance() {
        return INSTANCE;
    }
}
