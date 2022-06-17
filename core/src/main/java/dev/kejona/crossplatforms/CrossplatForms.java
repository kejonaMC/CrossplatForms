package dev.projectg.crossplatforms;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.projectg.crossplatforms.action.BedrockTransferAction;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.command.custom.CustomCommandManager;
import dev.projectg.crossplatforms.command.defaults.DefaultCommands;
import dev.projectg.crossplatforms.command.defaults.HelpCommand;
import dev.projectg.crossplatforms.command.defaults.ListCommand;
import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.config.ConfigurationModule;
import dev.projectg.crossplatforms.config.GeneralConfig;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FloodgateHandler;
import dev.projectg.crossplatforms.handler.GeyserHandler;
import dev.projectg.crossplatforms.handler.Placeholders;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.Interfacer;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.FormImageSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.ComponentSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bstats.charts.SimplePie;
import org.geysermc.cumulus.util.FormImage;

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
    private final BedrockHandler bedrockHandler;

    private final boolean bedrockSupport;

    private final Interfacer interfacer;

    private final CommandManager<CommandOrigin> commandManager;
    private final Command.Builder<CommandOrigin> commandBuilder;

    private final Placeholders placeholders;

    private final boolean success = true;

    public CrossplatForms(Logger logger,
                          Path dataFolder,
                          ServerHandler serverHandler,
                          String defaultCommand,
                          CommandManager<CommandOrigin> commandManager,
                          Placeholders placeholders,
                          CrossplatFormsBootstrap bootstrap) {
        long start = System.currentTimeMillis();
        INSTANCE = this;
        this.serverHandler = serverHandler;
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
                builder.registerExact(FormImage.class, new FormImageSerializer());
                builder.registerExact(CustomComponent.class, new ComponentSerializer());
            });
            configManager.getActionSerializer().genericAction(BedrockTransferAction.TYPE, BedrockTransferAction.class);
        }
        bootstrap.preConfigLoad(configManager); // allow implementation to add extra serializers, configs, actions, etc
        if (!configManager.load()) {
            logger.severe("A severe configuration error occurred, which will lead to significant parts of this plugin not loading. Please repair the config and run /forms reload or restart the server.");
        }
        Optional<GeneralConfig> generalConfig = configManager.getConfig(GeneralConfig.class);
        logger.debug("Took " + (System.currentTimeMillis() - configTime) + "ms to load config files.");

        // Load forms and menus from the configs into registries
        long registryTime = System.currentTimeMillis();
        interfacer.load(
            new BedrockFormRegistry(configManager, serverHandler),
            new JavaMenuRegistry(configManager, serverHandler)
        );
        logger.debug("Took " + (System.currentTimeMillis() - registryTime) + "ms to setup registries.");

        // Command defined in config or default provided by implementation
        String rootCommand = generalConfig.map(GeneralConfig::getRootCommand).orElse(defaultCommand);

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
