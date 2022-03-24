package dev.projectg.crossplatforms.command.custom;

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

import java.util.HashMap;
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

    /**
     * Only stored registered commands, not intercept commands.
     * @see CommandType
     */
    private final Map<String, CustomCommand> registeredCommands = new HashMap<>();

    public CustomCommandManager(CrossplatForms forms, CommandManager<CommandOrigin> commandManager) {
        this.configManager = forms.getConfigManager();
        this.commandManager = commandManager;
        this.serverHandler = forms.getServerHandler();
        this.interfaceManager = forms.getInterfaceManager();
        this.bedrockHandler = forms.getBedrockHandler();

        load();
        ReloadableRegistry.register(this);
    }

    private void load() {
        if (!configManager.getConfig(GeneralConfig.class).isPresent()) {
            return;
        }
        GeneralConfig config = configManager.getConfig(GeneralConfig.class).get();
        commandManager.setSetting(CommandManager.ManagerSettings.ALLOW_UNSAFE_REGISTRATION, config.isUnsafeCommandRegistration());
        registeredCommands.clear();

        for (CustomCommand command : config.getCommands().values()) {
            String name = command.getIdentifier();
            if (command.getPermission() == null || command.getPermission().isEmpty()) {
                command.setPermission(Constants.Id() + ".shortcut." + name);
            }
            CommandType type = command.getMethod();
            if (type == CommandType.REGISTER) {
                if (!(command instanceof RegisteredCommand)) {
                    throw new IllegalStateException("CustomCommand has method type REGISTER but is not a RegisteredCommand: " + command);
                }

                if (commandManager.getCommandTree().getNamedNode(name) == null) {
                    // only register if it hasn't been yet
                    // any references to the command are done through the map so that it can be updated after a reload
                    if (commandManager.isCommandRegistrationAllowed()) {
                        Logger.getLogger().debug("Registering shortcut command: " + command.getIdentifier());
                        commandManager.command(commandManager.commandBuilder(name)
                                .permission(origin -> hasPermission(origin, name))
                                .handler((context) -> {
                                    Logger.getLogger().debug("Executing registered command on thread: " + Thread.currentThread());
                                    FormPlayer player = Objects.requireNonNull(serverHandler.getPlayer(context.getSender().getUUID().orElseThrow(NoSuchElementException::new)));
                                    registeredCommands.get(name).run(player, interfaceManager, bedrockHandler);
                                })
                        );
                    } else {
                        Logger.getLogger().warn("Unable to register shortcut command '" + name + "' because registration is no longer possible. Restart the server for changes to apply.");
                    }
                }

                registeredCommands.put(name, command); // store the command or update it
            } else if (type == CommandType.INTERCEPT_CANCEL || type == CommandType.INTERCEPT_PASS) {
                if (command instanceof InterceptCommand) {
                    serverHandler.registerProxyCommand((InterceptCommand) command);
                } else {
                    throw new IllegalStateException("CustomCommand has method type INTERCEPT_CANCEL or INTERCEPT_PASS but is not a ProxiedCommand: " + command);
                }
            } else {
                Logger.getLogger().severe("Unhandled CommandType enum: " + type);
            }
        }
    }

    private boolean hasPermission(CommandOrigin origin, String commandName) {
        CustomCommand target = registeredCommands.get(commandName);
        if (target == null) {
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
        serverHandler.clearProxyCommands();
        load();
        return true;
    }
}
