package dev.projectg.crossplatforms.spigot;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.collect.ImmutableMap;
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

import java.util.Map;
import java.util.Set;

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

        // Bungee channel for selector
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        PlaceholderHandler placeholders;
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholders = new PlaceholderAPIHandler();
        } else {
            logger.warn("This plugin works best with PlaceholderAPI! Since you don't have it installed, only %player_name% and %player_uuid% will work in the GeyserHub config!");
            placeholders = new BasicPlaceholders();
        }

        // additional actions to register
        Map<String, Class<? extends Action>> actions = ImmutableMap.of("server", ServerAction.class);

        // configs to load
        Set<ConfigId> configs = ConfigId.defaults();
        configs.add(new ConfigId("access-items.yml", AccessItemConfig.VERSION, AccessItemConfig.MINIMUM_VERSION, AccessItemConfig.class, AccessItemConfig::updater));

        crossplatForms = new CrossplatForms(
                logger,
                getDataFolder().toPath(),
                configs,
                serverHandler,
                commandManager,
                placeholders,
                actions);

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
