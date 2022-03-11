package dev.projectg.crossplatforms.velocity;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.velocity.VelocityCommandManager;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.projectg.crossplatforms.BasicPlaceholders;
import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.CrossplatFormsBoostrap;
import dev.projectg.crossplatforms.JavaUtilLogger;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.config.serializer.KeyedTypeSerializer;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.velocity.handler.VelocityCommandOrigin;
import dev.projectg.crossplatforms.velocity.handler.VelocityInterfacer;
import dev.projectg.crossplatforms.velocity.handler.VelocityServerHandler;
import lombok.Getter;

import java.nio.file.Path;

@Plugin(id = "crossplatforms",
        name = "CrossplatForms-Velocity",
        version = "0.3.0",
        url = "https://github.com/ProjectG-Plugins/CrossplatForms",
        description = "Unifies inventory menus with Bedrock Edition forms.",
        authors = "Konicai",
        dependencies = {@Dependency(id = "Geyser-Velocity", optional = true), @Dependency(id = "floodgate", optional = true)})
public class CrossplatFormsVelocity implements CrossplatFormsBoostrap {

    private static CrossplatFormsVelocity INSTANCE;

    static {
        Constants.setId("crossplatformsvelocity"); // todo: this can probably be improved
    }

    @Getter
    private final ProxyServer server;
    private final PluginContainer container;
    private final Path dataFolder;
    private final Logger logger;

    private final VelocityServerHandler serverHandler;

    private CrossplatForms crossplatForms;

    @Inject
    public CrossplatFormsVelocity(ProxyServer server, PluginContainer container, Path dataFolder, java.util.logging.Logger logger) {
        INSTANCE = this;
        this.server = server;
        this.container = container;
        this.dataFolder = dataFolder;
        this.logger = new JavaUtilLogger(logger);

        serverHandler = new VelocityServerHandler(server);
    }

    @Subscribe
    public void load(ProxyInitializeEvent event) {
        if (crossplatForms != null) {
            logger.warn("Initializing already occurred");
        }

        // Yes, this is not Paper-exclusive plugin. Cloud handles this gracefully.
        VelocityCommandManager<CommandOrigin> commandManager;
        try {
            commandManager = new VelocityCommandManager<>(
                    container,
                    server,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    (VelocityCommandOrigin::new),
                    (origin -> (CommandSource) origin.getHandle())
            );
        } catch (Exception e) {
            logger.severe("Failed to create CommandManager, stopping");
            e.printStackTrace();
            return;
        }

        logger.warn("CrossplatForms-Velocity does not yet support placeholder plugins, only %player_name% and %player_uuid% will work (typically).");
        PlaceholderHandler placeholders = new BasicPlaceholders();

        crossplatForms = new CrossplatForms(
                logger,
                dataFolder,
                serverHandler,
                commandManager,
                placeholders,
                this
        );

        if (!crossplatForms.isSuccess()) {
            return;
        }

        server.getEventManager().register(this, serverHandler); // events for catching proxy commands
    }

    @Override
    public void preConfigLoad(ConfigManager configManager) {
        // register java menu config once java menus are supported

        KeyedTypeSerializer<Action> actionSerializer = configManager.getActionSerializer();
        actionSerializer.registerSimpleType(ServerAction.IDENTIFIER, String.class, ServerAction::new);
    }

    @Override
    public InterfaceManager interfaceManager(BedrockHandler bedrockHandler, BedrockFormRegistry bedrockRegistry, JavaMenuRegistry menuRegistry) {
        return new VelocityInterfacer(serverHandler, bedrockHandler, bedrockRegistry, menuRegistry);
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        server.getEventManager().unregisterListeners(this);
    }

    public static CrossplatFormsVelocity getInstance() {
        return INSTANCE;
    }
}
