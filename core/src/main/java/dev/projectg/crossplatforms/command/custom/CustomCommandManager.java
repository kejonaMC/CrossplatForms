package dev.projectg.crossplatforms.command.custom;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.config.GeneralConfig;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.reloadable.Reloadable;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

public class CustomCommandManager implements Reloadable {

    private final ConfigManager configManager;
    private final CommandManager<CommandOrigin> commandManager;
    private final ServerHandler serverHandler;
    private final InterfaceManager interfaceManager;
    private final BedrockHandler bedrockHandler;
    private final Logger logger;

    private final Map<Arguments, RegisteredCommand> registeredCommands = new HashMap<>();

    public CustomCommandManager(CrossplatForms forms, CommandManager<CommandOrigin> commandManager) {
        this.configManager = forms.getConfigManager();
        this.commandManager = commandManager;
        this.serverHandler = forms.getServerHandler();
        this.interfaceManager = forms.getInterfaceManager();
        this.bedrockHandler = forms.getBedrockHandler();
        this.logger = Logger.getLogger();

        load();
        ReloadableRegistry.register(this);
    }

    private void load() {
        serverHandler.clearProxyCommands();
        if (!configManager.getConfig(GeneralConfig.class).isPresent()) {
            return;
        }
        GeneralConfig config = configManager.getConfig(GeneralConfig.class).get();
        commandManager.setSetting(CommandManager.ManagerSettings.ALLOW_UNSAFE_REGISTRATION, config.isUnsafeCommandRegistration());

        List<Arguments> currentCommands = new ArrayList<>();
        for (CustomCommand command : config.getCommands().values()) {
            if (command == null) {
                continue;
            }

            CommandType type = command.getMethod();
            if (type == CommandType.REGISTER) {
                if (command instanceof RegisteredCommand) {
                    RegisteredCommand registered = (RegisteredCommand) command;
                    registerCommand(registered);
                    currentCommands.add(registered.literals());
                } else {
                    throw new IllegalStateException("CustomCommand has method type REGISTER but is not a RegisteredCommand: " + command);
                }
            } else if (type == CommandType.INTERCEPT_CANCEL || type == CommandType.INTERCEPT_PASS) {
                if (command instanceof InterceptCommand) {
                    interceptCommand((InterceptCommand) command);
                } else {
                    throw new IllegalStateException("CustomCommand has method type INTERCEPT_CANCEL or INTERCEPT_PASS but is not a ProxiedCommand: " + command);
                }
            } else {
                logger.severe("Unhandled CommandType enum: " + type + ". Not registering command with name: " + command.getIdentifier());
            }
        }

        for (Map.Entry<Arguments, RegisteredCommand> entry : registeredCommands.entrySet()) {
            // enable commands that are current
            // disable commands that are no longer current
            // cannot remove old commands from the map because a double reload could result in cloud raising exceptions due to duplicate nodes/arguments
            entry.getValue().enable(currentCommands.contains(entry.getKey()));
        }
    }

    private void registerCommand(RegisteredCommand command) {
        Objects.requireNonNull(command);
        final String name = command.getIdentifier();
        final Arguments literals = command.literals();
        final String[] array = literals.source();


        if (command.getPermission() == null || command.getPermission().isEmpty()) {
            command.setPermission(Constants.Id() + ".shortcut." + name);
        }

        if (literals.length < 1) {
            logger.warn("Cannot register custom command '" + name + "' because its length is less than 1");
            return;
        }

        if (!registeredCommands.containsKey(literals)) {
            if (commandManager.isCommandRegistrationAllowed()) {

                Command.Builder<CommandOrigin> builder = commandManager.commandBuilder(array[0]);
                for (int i = 1; i < literals.length; i++) {
                    // build the command with all the literal arguments
                    builder = builder.literal(array[i]);
                }

                try {
                    commandManager.command(builder
                        .permission(origin -> hasPermission(origin, literals))
                        .handler((context) -> executeCommand(context.getSender(), literals))
                    );
                    registeredCommands.put(literals, command); // set definition
                } catch (Exception e) {
                    logger.warn("Failed to register custom command '" + name + "', likely because it already exists: " + Arrays.toString(array));
                    logger.warn(e.getMessage());
                    if (logger.isDebug()) {
                        e.printStackTrace();
                    } else {
                        logger.warn("Enable debug for further information");
                    }
                }
            } else {
                logger.warn("Unable to register shortcut command '" + name + "' because registration is no longer possible. Restart the server for changes to apply.");
            }
        } else {
            // already setup, just update the definition.
            registeredCommands.put(literals, command);
        }
    }

    private void executeCommand(CommandOrigin player, Arguments command) {
        logger.debug("Executing registered command on thread: " + Thread.currentThread());
        FormPlayer target = Objects.requireNonNull(serverHandler.getPlayer(player.getUUID().orElseThrow(NoSuchElementException::new)));
        RegisteredCommand latest = registeredCommands.get(command);
        if (latest == null) {
            String message = "Unexpected state: command " + Arrays.toString(command.source()) + " no longer exists internally";
            target.sendMessage(message);
            logger.warn(message);
        } else if (latest.isEnabled()) {
            latest.run(target, interfaceManager, bedrockHandler);
        } else {
            target.sendMessage("That command is no longer available");
        }
    }

    private void interceptCommand(InterceptCommand command) {
        if (command.getPattern() == null && command.getExact() == null) {
            logger.severe("CustomCommand of method INTERCEPT_CANCEL or INTERCEPT_PASS defines both 'exact' and 'pattern': " + command + ". Not registering, as only one must be specified.");
        } else {
            serverHandler.registerProxyCommand(command);
        }
    }

    private boolean hasPermission(CommandOrigin origin, Arguments command) {
        RegisteredCommand target = registeredCommands.get(command);
        if (target == null || !target.isEnabled()) {
            // no longer present in the config
            return false;
        } else {
            if (origin.isPlayer()) {
                UUID uuid = origin.getUUID().orElseThrow(NullPointerException::new);
                return target.getPlatform().matches(uuid, bedrockHandler) && origin.hasPermission(target.getPermission());
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean reload() {
        load();
        return true;
    }
}
