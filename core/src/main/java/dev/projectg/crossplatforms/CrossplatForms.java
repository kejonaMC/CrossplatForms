package dev.projectg.crossplatforms;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.action.BedrockTransferAction;
import dev.projectg.crossplatforms.action.CommandsAction;
import dev.projectg.crossplatforms.action.InterfaceAction;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.command.defaults.DefaultCommands;
import dev.projectg.crossplatforms.command.defaults.HelpCommand;
import dev.projectg.crossplatforms.command.defaults.ListCommand;
import dev.projectg.crossplatforms.command.proxy.CustomCommandManager;
import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.config.GeneralConfig;
import dev.projectg.crossplatforms.config.serializer.KeyedTypeSerializer;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FloodgateHandler;
import dev.projectg.crossplatforms.handler.GeyserHandler;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.FormImageSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.ComponentSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.geysermc.cumulus.util.FormImage;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Getter
public class CrossplatForms {
    private static CrossplatForms INSTANCE;

    public static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    private final ConfigManager configManager;
    private final ServerHandler serverHandler;
    private final BedrockHandler bedrockHandler;

    private final boolean cumulusAvailable;

    private final InterfaceManager interfaceManager;

    private final CommandManager<CommandOrigin> commandManager;
    private final Command.Builder<CommandOrigin> commandBuilder;

    private final PlaceholderHandler placeholders;

    private final boolean success = true;

    public CrossplatForms(Logger logger,
                          Path dataFolder,
                          ServerHandler serverHandler,
                          CommandManager<CommandOrigin> commandManager,
                          PlaceholderHandler placeholders,
                          CrossplatFormsBoostrap bootstrap) {
        long start = System.currentTimeMillis();
        INSTANCE = this;
        this.serverHandler = serverHandler;
        this.commandManager = commandManager;
        this.placeholders = placeholders;
        ReloadableRegistry.clear();
        logger.info("Branch: " + Constants.BRANCH + ", Commit: " + Constants.COMMIT);

        if (serverHandler.isFloodgateEnabled() && !Boolean.getBoolean("CrossplatForms.IgnoreFloodgate")) {
            bedrockHandler = new FloodgateHandler();
            cumulusAvailable = true;
        } else if (serverHandler.isGeyserEnabled()) {
            bedrockHandler = new GeyserHandler();
            logger.warn("Floodgate is recommended and more stable!");
            cumulusAvailable = true;
        } else {
            bedrockHandler = BedrockHandler.empty();
            logger.severe("Geyser nor Floodgate are installed! There may be issues.");
            cumulusAvailable = false;
        }

        long configTime = System.currentTimeMillis();
        configManager = new ConfigManager(dataFolder, logger);
        configManager.register(ConfigId.GENERAL);
        if (cumulusAvailable) {
            configManager.register(ConfigId.BEDROCK_FORMS);
            configManager.serializers(builder -> {
                builder.registerExact(BedrockForm.class, new BedrockFormSerializer());
                builder.registerExact(FormImage.class, new FormImageSerializer());
                builder.registerExact(CustomComponent.class, new ComponentSerializer());
            });
        }
        registerDefaultActions(configManager);
        bootstrap.preConfigLoad(configManager);
        if (!configManager.load()) {
            logger.severe("A severe configuration error occurred, which will lead to significant parts of this plugin not loading. Please repair the config and run /forms reload or restart the server.");
        }
        logger.setDebug(configManager.getConfig(GeneralConfig.class).map(GeneralConfig::isEnableDebug).orElse(false));
        logger.debug("Took " + (System.currentTimeMillis() - configTime) + "ms to load config files.");

        // Load forms
        long registryTime = System.currentTimeMillis();
        interfaceManager = bootstrap.interfaceManager(
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
                .permission(FormsCommand.PERMISSION_BASE + "base")
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
        new CustomCommandManager(this, commandManager);

        logger.info("Took " + (System.currentTimeMillis() - start) + "ms to boot CrossplatForms.");
    }


    public static CrossplatForms getInstance() {
        return INSTANCE;
    }

    public static void registerDefaultActions(ConfigManager configManager) {
        KeyedTypeSerializer<Action> actionSerializer = configManager.getActionSerializer();
        actionSerializer.registerSimpleType(InterfaceAction.TYPE, String.class, InterfaceAction::new);
        actionSerializer.registerSimpleType(CommandsAction.TYPE, new TypeToken<List<DispatchableCommand>>() {}, CommandsAction::new);
        actionSerializer.registerType(BedrockTransferAction.TYPE, BedrockTransferAction.class);
    }
}
