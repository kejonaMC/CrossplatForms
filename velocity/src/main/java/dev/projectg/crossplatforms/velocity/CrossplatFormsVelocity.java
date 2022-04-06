package dev.projectg.crossplatforms.velocity;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.velocity.VelocityCommandManager;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.projectg.crossplatforms.handler.BasicPlaceholders;
import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.CrossplatFormsBoostrap;
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
import org.bstats.charts.CustomChart;
import org.bstats.velocity.Metrics;

import java.nio.file.Path;

public class CrossplatFormsVelocity implements CrossplatFormsBoostrap {

    private static final int BSTATS_ID = 14708;
    private static CrossplatFormsVelocity INSTANCE;

    static {
        // load information from build.properties
        Constants.fetch();
        Constants.setId("crossplatformsvelocity");
    }

    @Getter
    private final ProxyServer server;
    private final PluginContainer container;
    private final Path dataFolder;
    private final Logger logger;
    private final Metrics.Factory metricsFactory;

    private final VelocityServerHandler serverHandler;

    private CrossplatForms crossplatForms;
    private Metrics metrics;

    @Inject
    public CrossplatFormsVelocity(ProxyServer server, PluginContainer container, @DataDirectory Path dataFolder, org.slf4j.Logger logger, Metrics.Factory metricsFactory) {
        INSTANCE = this;
        this.server = server;
        this.container = container;
        this.dataFolder = dataFolder;
        this.logger = new SLF4JLogger(logger);
        this.metricsFactory = metricsFactory;

        serverHandler = new VelocityServerHandler(server);
    }

    @Subscribe
    public void load(ProxyInitializeEvent event) {
        if (crossplatForms != null) {
            logger.warn("Initializing already occurred");
        }
        metrics = metricsFactory.make(this, BSTATS_ID);

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
                "formsv",
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

    @Override
    public void addCustomChart(CustomChart chart) {
        metrics.addCustomChart(chart);
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        server.getEventManager().unregisterListeners(this);
    }

    public static CrossplatFormsVelocity getInstance() {
        return INSTANCE;
    }
}
