package dev.projectg.crossplatforms.spigot;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import dev.projectg.crossplatforms.BasicPlaceholders;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.JavaUtilLogger;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.SpigotCommandOrigin;
import dev.projectg.crossplatforms.command.defaults.InspectCommand;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuListeners;
import dev.projectg.crossplatforms.item.AccessItem;
import dev.projectg.crossplatforms.item.AccessItemListeners;
import dev.projectg.crossplatforms.item.AccessItemRegistry;
import dev.projectg.crossplatforms.spigot.handler.SpigotServerHandler;
import dev.projectg.crossplatforms.spigot.handler.PlaceholderAPIHandler;
import dev.projectg.crossplatforms.utils.PlaceholderHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;

public class CrossplatFormsSpigot extends JavaPlugin {

    private BukkitAudiences audiences;
    private CrossplatForms crossplatForms;

    @Override
    public void onEnable() {
        Logger logger = new JavaUtilLogger(Bukkit.getLogger());
        if (crossplatForms != null) {
            logger.warn("Bukkit reloading is NOT supported!");
        }
        audiences = BukkitAudiences.create(this);
        ServerHandler serverHandler = new SpigotServerHandler(this, audiences);

        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            logger.warn("This plugin works best with PlaceholderAPI! Since you don't have it installed, only %player_name% and %player_uuid% will work in the GeyserHub config!");
        }

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
            placeholders = new BasicPlaceholders();
        }

        crossplatForms = new CrossplatForms(
                logger,
                getDataFolder().toPath(),
                serverHandler,
                commandManager,
                placeholders);

        if (!crossplatForms.isSuccess()) {
            return;
        }

        AccessItemRegistry accessItemRegistry = new AccessItemRegistry(crossplatForms.getConfigManager(), serverHandler);

        // addon to the inspect command
        commandManager.command(crossplatForms.getCommandBuilder()
                .literal(InspectCommand.NAME)
                .permission(InspectCommand.PERMISSION)
                .literal("item")
                .argument(StringArgument.<CommandOrigin>newBuilder("item")
                        .withSuggestionsProvider(((context, s) -> accessItemRegistry.getItems().values()
                                .stream()
                                .map(AccessItem::getIdentifier)
                                .collect(Collectors.toList()))))
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String name = context.get("item");
                    AccessItem item = accessItemRegistry.getItems().get(name);
                    if (item == null) {
                        origin.sendMessage(Logger.Level.SEVERE, "That Access Item doesn't exist!");
                    } else {
                        origin.sendMessage(Logger.Level.INFO, "Inspection of access item: " + name);
                        origin.sendMessage(Logger.Level.INFO, item.toString());
                    }
                })
        );

        // Registering events required to manage access items
        Bukkit.getServer().getPluginManager().registerEvents(
                new AccessItemListeners(
                        crossplatForms.getInterfaceManager(),
                        accessItemRegistry,
                        crossplatForms.getBedrockHandler()),
                this);

        // events regarding inventory GUI menus
        Bukkit.getServer().getPluginManager().registerEvents(new JavaMenuListeners(crossplatForms.getInterfaceManager()), this);
    }

    @Override
    public void onDisable() {
        if (audiences != null) {
            audiences.close();
        }

        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }
}
