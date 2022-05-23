package dev.projectg.crossplatforms.spigot;

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
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.Interfacer;
import dev.projectg.crossplatforms.spigot.common.CloseMenuAction;
import dev.projectg.crossplatforms.spigot.common.PlaceholderAPIHandler;
import dev.projectg.crossplatforms.spigot.common.ServerAction;
import dev.projectg.crossplatforms.spigot.common.SpigotCommandOrigin;
import dev.projectg.crossplatforms.spigot.common.SpigotCommon;
import dev.projectg.crossplatforms.spigot.common.SpigotInterfacer;
import dev.projectg.crossplatforms.spigot.common.SpigotServerHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.CustomChart;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CrossplatFormsSpigot extends JavaPlugin implements CrossplatFormsBootstrap, Listener {

    private static CrossplatFormsSpigot INSTANCE;

    static {
        // load information from build.properties
        Constants.fetch();
    }

    private CrossplatForms crossplatForms;
    private BukkitAudiences audiences;
    private Metrics metrics;

    @Override
    public void onEnable() {
        INSTANCE = this;
        metrics = new Metrics(this, SpigotCommon.METRICS_ID);
        ServerAction.SENDER = this; // hack to have ServerAction in common module
        Logger logger = new JavaUtilLogger(getLogger());
        if (crossplatForms != null) {
            logger.warn("Bukkit reloading is NOT supported!");
        }
        audiences = BukkitAudiences.create(this);
        ServerHandler serverHandler = new SpigotServerHandler(this, audiences);

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

        try {
            // Brigadier is ideal if possible. Allows for much more readable command options, especially on BE.
            commandManager.registerBrigadier();
        } catch (BukkitCommandManager.BrigadierFailureException e) {
            logger.warn("Failed to initialize Brigadier support: " + e.getMessage());
        }

        // For ServerAction
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        PlaceholderHandler placeholders;
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
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

        SpigotAccessItems accessItemRegistry = new SpigotAccessItems(
                this,
                crossplatForms.getConfigManager(),
                serverHandler,
                crossplatForms.getInterfacer(),
                crossplatForms.getBedrockHandler(),
                placeholders
        );

        // Registering events required to manage access items
        Bukkit.getServer().getPluginManager().registerEvents(accessItemRegistry, this);

        // Commands added by access items
        new GiveCommand(crossplatForms, accessItemRegistry).register(commandManager, crossplatForms.getCommandBuilder());
        new InspectItemCommand(crossplatForms, accessItemRegistry).register(commandManager, crossplatForms.getCommandBuilder());

        // bstats
        addCustomChart(new SimplePie(SpigotCommon.PIE_CHART_LEGACY, () -> "false")); // not legacy
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
        Bukkit.getServer().getPluginManager().registerEvents(manager, this);
        return manager;
    }

    @Override
    public void onDisable() {
        if (audiences != null) {
            audiences.close();
        }

        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    @Override
    public void addCustomChart(CustomChart chart) {
        metrics.addCustomChart(chart);
    }

    public static CrossplatFormsSpigot getInstance() {
        return INSTANCE;
    }
}
