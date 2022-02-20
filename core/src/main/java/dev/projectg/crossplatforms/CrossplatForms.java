package dev.projectg.crossplatforms;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.command.defaults.DefaultCommands;
import dev.projectg.crossplatforms.command.defaults.HelpCommand;
import dev.projectg.crossplatforms.command.defaults.ListCommand;
import dev.projectg.crossplatforms.command.proxy.ProxyCommandManager;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.config.GeneralConfig;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FloodgateHandler;
import dev.projectg.crossplatforms.handler.GeyserHandler;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;
import dev.projectg.crossplatforms.utils.PlaceholderHandler;
import lombok.Getter;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.session.auth.AuthType;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

@Getter
public class CrossplatForms {
    private static CrossplatForms INSTANCE;

    private ConfigManager configManager;
    private final ServerHandler serverHandler;
    private BedrockHandler bedrockHandler;

    private InterfaceManager interfaceManager;

    private final CommandManager<CommandOrigin> commandManager;
    private Command.Builder<CommandOrigin> commandBuilder;

    private final PlaceholderHandler placeholders;

    private boolean success = true;

    public CrossplatForms(Logger logger,
                          Path dataFolder,
                          ServerHandler serverHandler,
                          CommandManager<CommandOrigin> commandManager,
                          PlaceholderHandler placeholders) {
        long start = System.currentTimeMillis();
        INSTANCE = this;
        this.serverHandler = serverHandler;
        this.commandManager = commandManager;
        this.placeholders = placeholders;
        ReloadableRegistry.clear();
        logger.info("Branch: " + Constants.BRANCH + ", Commit: " + Constants.COMMIT);

        if (serverHandler.isFloodgateEnabled()) {
            if (serverHandler.isGeyserEnabled() && GeyserImpl.getInstance().getConfig().getRemote().getAuthType() != AuthType.FLOODGATE ) {
                logger.warn("Floodgate is installed but auth-type in Geyser's config is not set to Floodgate! Ignoring Floodgate.");
                bedrockHandler = new GeyserHandler();
            } else {
                bedrockHandler = new FloodgateHandler();
            }
        } else if (serverHandler.isGeyserEnabled()) {
            bedrockHandler = new GeyserHandler();
            logger.warn("Floodgate is recommended and more stable!");
        } else {
            logger.severe("Geyser nor Floodgate are installed! Disabling...");
            // todo: make this feasible
            success = false;
            return;
        }

        long configTime = System.currentTimeMillis();
        configManager = new ConfigManager(dataFolder, logger);
        if (!configManager.loadAllConfigs()) {
            logger.severe("A severe configuration error occurred, which will lead to significant parts of this plugin not loading. Please repair the config and run /forms reload or restart the server.");
        }
        logger.setDebug(configManager.getConfig(GeneralConfig.class).map(GeneralConfig::isEnableDebug).orElse(false));
        logger.debug("Took " + (System.currentTimeMillis() - configTime) + "ms to load config files.");

        // Load forms
        long registryTime = System.currentTimeMillis();
        interfaceManager = new InterfaceManager(
                serverHandler,
                bedrockHandler,
                new BedrockFormRegistry(configManager, serverHandler),
                new JavaMenuRegistry(configManager, serverHandler)
        );
        logger.debug("Took " + (System.currentTimeMillis() - registryTime) + "ms to setup registries.");

        // Makes the info messages for invalid syntax, sender, etc exceptions nicer
        new MinecraftExceptionHandler<CommandOrigin>()
                .withArgumentParsingHandler()
                .withInvalidSenderHandler()
                .withInvalidSyntaxHandler()
                .withNoPermissionHandler()
                .withCommandExecutionHandler()
                .apply(commandManager, (serverHandler::asAudience));


        MinecraftHelp<CommandOrigin> minecraftHelp = new MinecraftHelp<>(
                "/" + FormsCommand.NAME + " help",
                (serverHandler::asAudience),
                commandManager
        );

        // The top of our command tree
        commandBuilder = commandManager.commandBuilder(FormsCommand.NAME);

        // The handler for the root /forms command
        commandManager.command(commandBuilder
                .permission("crossplatforms.command.base")
                .handler((context -> {
                    CommandOrigin origin = context.getSender();
                    try {
                        if (origin.hasPermission(ListCommand.PERMISSION)) {
                            logger.debug("Executing /forms list from /forms");
                            commandManager.executeCommand(origin, FormsCommand.NAME + " list").get();
                        } else if (origin.hasPermission(HelpCommand.PERMISSION)) {
                            minecraftHelp.queryCommands("", context.getSender());
                        } else {
                            origin.sendMessage(Logger.Level.INFO, "Please specify a sub command");
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }))
                .build());

        for (FormsCommand command : new DefaultCommands(this, minecraftHelp).getCommands()) {
            // Registering sub commands
            command.register(commandManager, commandBuilder);
        }

        // register shortcut commands
        new ProxyCommandManager(this, commandManager);



        logger.info("Took " + (System.currentTimeMillis() - start) + "ms to boot CrossplatForms.");
    }


    public static CrossplatForms getInstance() {
        return INSTANCE;
    }
}
