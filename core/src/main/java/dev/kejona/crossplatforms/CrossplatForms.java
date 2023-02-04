package dev.kejona.crossplatforms;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.kejona.crossplatforms.action.BedrockTransferAction;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.FormsCommand;
import dev.kejona.crossplatforms.command.custom.CustomCommandManager;
import dev.kejona.crossplatforms.command.defaults.DefaultCommands;
import dev.kejona.crossplatforms.command.defaults.HelpCommand;
import dev.kejona.crossplatforms.command.defaults.ListCommand;
import dev.kejona.crossplatforms.config.ConfigId;
import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.config.ConfigurationModule;
import dev.kejona.crossplatforms.config.GeneralConfig;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FloodgateHandler;
import dev.kejona.crossplatforms.handler.GeyserHandler;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockFormSerializer;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.ComponentSerializer;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import dev.kejona.crossplatforms.interfacing.java.JavaMenuRegistry;
import dev.kejona.crossplatforms.item.InventoryFactory;
import dev.kejona.crossplatforms.permission.Permissions;
import dev.kejona.crossplatforms.reloadable.ReloadableRegistry;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bstats.charts.SimplePie;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Getter
public class CrossplatForms {
    private static CrossplatForms INSTANCE;

    public static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();
    public static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    private final ConfigManager configManager;
    private final ServerHandler serverHandler;
    private final Permissions permissions;
    private final BedrockHandler bedrockHandler;

    private final boolean bedrockSupport;

    private final Interfacer interfacer;

    private final CommandManager<CommandOrigin> commandManager;
    private final Command.Builder<CommandOrigin> commandBuilder;
    private final String rootCommand;

    private final Placeholders placeholders;

    @Deprecated
    private final boolean success = true;

    public CrossplatForms(Logger logger,
                          Path dataFolder,
                          ServerHandler serverHandler,
                          Permissions permissions,
                          String defaultCommand,
                          CommandManager<CommandOrigin> commandManager,
                          Placeholders placeholders,
                          InventoryFactory inventoryFactory,
                          CrossplatFormsBootstrap bootstrap) {
        long start = System.currentTimeMillis();
        if (INSTANCE != null) {
            logger.severe("CrossplatForms has already been instantiated! There may be unexpected issues.");
        }
        INSTANCE = this;
        this.serverHandler = serverHandler;
        this.permissions = permissions;
        this.commandManager = commandManager;
        this.placeholders = placeholders;
        ReloadableRegistry.clear();
        logger.info("Version: " + Constants.version() + ", Branch: " + Constants.branch() + ", Build: " + Constants.buildNumber() + ", Commit: " + Constants.commit());

        // Decide on which implementation to deal with bedrock players
        if (serverHandler.isFloodgateEnabled() && !Boolean.getBoolean("CrossplatForms.IgnoreFloodgate")) {
            bedrockHandler = new FloodgateHandler();
            bedrockSupport = true;
        } else if (serverHandler.isGeyserEnabled() && !Boolean.getBoolean("CrossplatForms.IgnoreGeyser")) {
            // java 16 GeyserHandler should always be instantiated here since Geyser can only run on java 16+
            logger.warn("Floodgate is recommended and less likely to break with new updates!");
            if (GeyserHandler.supported()) {
                bedrockHandler = new GeyserHandler();
                bedrockSupport = true;
            } else {
                logger.warn("This platform does not appear to support multi release jars, add '-Djdk.util.jar.enableMultiRelease=force' to your JVM flags in order to use Geyser.");
                bedrockHandler = BedrockHandler.empty();
                bedrockSupport = false;
            }
        } else {
            bedrockHandler = BedrockHandler.empty();
            bedrockSupport = false;
        }

        if (!bedrockSupport) {
            logger.warn("No Bedrock Handler being used! There may be issues.");
        }

        interfacer = bootstrap.interfaceManager();
        Injector injector = Guice.createInjector(
            new ConfigurationModule(
                interfacer,
                inventoryFactory,
                bedrockHandler,
                serverHandler,
                placeholders
            )
        );

        // Load all configs
        long configTime = System.currentTimeMillis();
        configManager = new ConfigManager(dataFolder, logger, injector);
        configManager.registerPriority(ConfigId.GENERAL);
        if (bedrockSupport) {
            // Only register bedrock form features and only references cumulus classes if cumulus is available
            configManager.register(ConfigId.BEDROCK_FORMS);
            configManager.serializers(builder -> {
                builder.registerExact(BedrockForm.class, new BedrockFormSerializer());
                builder.registerExact(CustomComponent.class, new ComponentSerializer());
            });
            BedrockTransferAction.register(configManager.getActionSerializer());
        }
        bootstrap.preConfigLoad(configManager); // allow implementation to add extra serializers, configs, actions, etc
        if (!configManager.load()) {
            logger.severe("A severe configuration error occurred, which will lead to significant parts of this plugin not loading. Please repair the config and run /forms reload or restart the server.");
        }
        Optional<GeneralConfig> generalConfig = configManager.getConfig(GeneralConfig.class);
        logger.debug("Took " + (System.currentTimeMillis() - configTime) + "ms to load config files.");

        // Load forms and menus from the configs into registries
        interfacer.load(
            new BedrockFormRegistry(configManager, permissions),
            new JavaMenuRegistry(configManager, permissions)
        );

        // Command defined in config or default provided by implementation
        rootCommand = generalConfig.map(GeneralConfig::getRootCommand).orElse(defaultCommand);

        // Makes the info messages for invalid syntax, sender, etc exceptions nicer
        new MinecraftExceptionHandler<CommandOrigin>()
                .withArgumentParsingHandler()
                .withInvalidSenderHandler()
                .withInvalidSyntaxHandler()
                .withNoPermissionHandler()
                .withCommandExecutionHandler()
                .apply(commandManager, (serverHandler::asAudience));


        MinecraftHelp<CommandOrigin> minecraftHelp = new MinecraftHelp<>(
                "/" + rootCommand + " help",
                (serverHandler::asAudience),
                commandManager
        );

        // The top of our command tree
        commandBuilder = commandManager.commandBuilder(rootCommand);

        // The handler for the root /forms command
        commandManager.command(commandBuilder
                .permission(FormsCommand.PERMISSION_BASE + "base")
                .handler((context -> {
                    CommandOrigin origin = context.getSender();
                    try {
                        if (origin.hasPermission(ListCommand.PERMISSION)) {
                            logger.debug("Executing /forms list from /forms");
                            // todo: don't need to wait for command result
                            commandManager.executeCommand(origin, rootCommand + " list").get();
                        } else if (origin.hasPermission(HelpCommand.PERMISSION)) {
                            minecraftHelp.queryCommands("", context.getSender());
                        } else {
                            origin.warn("Please specify a sub command");
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

        // extra charts for bstats
        bootstrap.addCustomChart(new SimplePie("bedrockHandler", bedrockHandler::getType));

        logger.info("Took " + (System.currentTimeMillis() - start) + "ms to boot CrossplatForms.");
    }


    public static CrossplatForms getInstance() {
        return INSTANCE;
    }
}
