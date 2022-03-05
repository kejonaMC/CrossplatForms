package dev.projectg.crossplatforms.spigot;

import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import dev.projectg.crossplatforms.BasicPlaceholders;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.JavaUtilLogger;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.accessitem.AccessItemConfig;
import dev.projectg.crossplatforms.accessitem.GiveCommand;
import dev.projectg.crossplatforms.accessitem.InspectItemCommand;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.config.KeyedTypeSerializer;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.spigot.handler.PlaceholderAPIHandler;
import dev.projectg.crossplatforms.spigot.handler.SpigotAccessItemRegistry;
import dev.projectg.crossplatforms.spigot.handler.SpigotCommandOrigin;
import dev.projectg.crossplatforms.spigot.handler.SpigotServerHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CrossplatFormsSpigot extends JavaPlugin {

    private static CrossplatFormsSpigot INSTANCE;

    private BukkitAudiences audiences;
    private CrossplatForms crossplatForms;

    @Override
    public void onEnable() {
        INSTANCE = this;
        Logger logger = new JavaUtilLogger(Bukkit.getLogger());
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
            placeholders = new PlaceholderAPIHandler();
        } else {
            logger.warn("This plugin works best with PlaceholderAPI! Since you don't have it installed, only %player_name% and %player_uuid% will work (typically).");
            placeholders = new BasicPlaceholders();
        }

        crossplatForms = new CrossplatForms(
                logger,
                getDataFolder().toPath(),
                serverHandler,
                commandManager,
                placeholders,
                this::preConfigLoad);

        if (!crossplatForms.isSuccess()) {
            return;
        }

        SpigotAccessItemRegistry accessItemRegistry = new SpigotAccessItemRegistry(
                crossplatForms.getConfigManager(),
                serverHandler,
                crossplatForms.getInterfaceManager(),
                crossplatForms.getBedrockHandler(),
                placeholders);

        // Registering events required to manage access items
        Bukkit.getServer().getPluginManager().registerEvents(accessItemRegistry, this);

        // Commands added by access items
        new GiveCommand(crossplatForms, accessItemRegistry).register(commandManager, crossplatForms.getCommandBuilder());
        new InspectItemCommand(crossplatForms, accessItemRegistry).register(commandManager, crossplatForms.getCommandBuilder());

        // events regarding inventory GUI menus
        Bukkit.getServer().getPluginManager().registerEvents(new MenuHelper(crossplatForms.getInterfaceManager()), this);
    }

    private void preConfigLoad(ConfigManager configManager) {
        configManager.register(new ConfigId(
                "access-items.yml",
                AccessItemConfig.VERSION,
                AccessItemConfig.MINIMUM_VERSION,
                AccessItemConfig.class,
                AccessItemConfig::updater));

        KeyedTypeSerializer<Action> actionSerializer = configManager.getActionSerializer();
        actionSerializer.registerSimpleType(ServerAction.IDENTIFIER, String.class, ServerAction::new);
    }

    @Override
    public void onDisable() {
        if (audiences != null) {
            audiences.close();
        }

        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public static CrossplatFormsSpigot getInstance() {
        return INSTANCE;
    }
}
