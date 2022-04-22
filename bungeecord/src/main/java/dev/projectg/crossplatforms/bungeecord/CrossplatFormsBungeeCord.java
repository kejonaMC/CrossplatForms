package dev.projectg.crossplatforms.bungeecord;

import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.handler.BasicPlaceholders;
import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.CrossplatFormsBoostrap;
import dev.projectg.crossplatforms.JavaUtilLogger;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.bungeecord.handler.BungeeCommandOrigin;
import dev.projectg.crossplatforms.bungeecord.handler.BungeeCordServerHandler;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.interfacing.NoMenusInterfacer;
import dev.projectg.crossplatforms.proxy.ProtocolizeInterfacer;
import dev.projectg.crossplatforms.serialize.KeyedTypeSerializer;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.CustomChart;

public class CrossplatFormsBungeeCord extends Plugin implements CrossplatFormsBoostrap {

    private static final int BSTATS_ID = 14706;
    private static CrossplatFormsBungeeCord INSTANCE;
    public static final BungeeComponentSerializer COMPONENT_SERIALIZER = BungeeComponentSerializer.get();

    static {
        // load information from build.properties
        Constants.fetch();
        Constants.setId("crossplatformsbungee");
    }

    private CrossplatForms crossplatForms;
    private BungeeAudiences audiences;
    private BungeeCordServerHandler serverHandler;
    private Metrics metrics;
    private boolean protocolizePresent;

    @Override
    public void onEnable() {
        INSTANCE = this;
        metrics = new Metrics(this, BSTATS_ID);
        Logger logger = new JavaUtilLogger(getLogger());
        if (crossplatForms != null) {
            logger.warn("Bukkit reloading is NOT supported!");
        }
        audiences = BungeeAudiences.create(this);
        serverHandler = new BungeeCordServerHandler(this, audiences);

        BungeeCommandManager<CommandOrigin> commandManager;
        try {
            commandManager = new BungeeCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    (BungeeCommandOrigin::new),
                    origin -> (CommandSender) origin.getHandle()
            );
        } catch (Exception e) {
            logger.severe("Failed to create CommandManager, stopping");
            e.printStackTrace();
            return;
        }

        logger.warn("CrossplatForms-BungeeCord does not yet support placeholder plugins, only %player_name% and %player_uuid% will work (typically).");
        PlaceholderHandler placeholders = new BasicPlaceholders();

        protocolizePresent = getProxy().getPluginManager().getPlugin("Protocolize") != null;

        crossplatForms = new CrossplatForms(
                logger,
                getDataFolder().toPath(),
                serverHandler,
                "formsb",
                commandManager,
                placeholders,
                this
        );

        if (!crossplatForms.isSuccess()) {
            return;
        }

        getProxy().getPluginManager().registerListener(this, serverHandler); // events for catching proxy commands
    }

    @Override
    public void preConfigLoad(ConfigManager configManager) {
        if (protocolizePresent) {
            configManager.register(ConfigId.JAVA_MENUS);
        }

        KeyedTypeSerializer<Action> actionSerializer = configManager.getActionSerializer();
        actionSerializer.registerSimpleType(ServerAction.IDENTIFIER, String.class, ServerAction::new);
    }

    @Override
    public InterfaceManager interfaceManager(BedrockHandler bedrockHandler, BedrockFormRegistry bedrockRegistry, JavaMenuRegistry menuRegistry) {
        if (protocolizePresent) {
            return new ProtocolizeInterfacer(serverHandler, bedrockHandler, bedrockRegistry, menuRegistry);
        } else {
            return new NoMenusInterfacer(serverHandler, bedrockHandler, bedrockRegistry, menuRegistry);
        }
    }

    @Override
    public void onDisable() {
        if (audiences != null) {
            audiences.close();
        }

        getProxy().getPluginManager().unregisterListeners(this);
    }

    @Override
    public void addCustomChart(CustomChart chart) {
        metrics.addCustomChart(chart);
    }

    public static CrossplatFormsBungeeCord getInstance() {
        return INSTANCE;
    }
}
