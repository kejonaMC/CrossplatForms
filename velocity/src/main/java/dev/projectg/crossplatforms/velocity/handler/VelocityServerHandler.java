package dev.projectg.crossplatforms.velocity.handler;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.command.proxy.CustomCommand;
import dev.projectg.crossplatforms.command.proxy.ProxyCommandCache;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import net.kyori.adventure.audience.Audience;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VelocityServerHandler extends ProxyCommandCache implements ServerHandler {

    private final ProxyServer server;
    private final CommandManager commandManager;

    public VelocityServerHandler(@Nonnull ProxyServer server) {
        this.server = server;
        this.commandManager = server.getCommandManager();
    }

    @Nullable
    @Override
    public FormPlayer getPlayer(UUID uuid) {
        return server.getPlayer(uuid).map(VelocityPlayer::new).orElse(null);
    }

    @Nullable
    @Override
    public FormPlayer getPlayer(String name) {
        return server.getPlayer(name).map(VelocityPlayer::new).orElse(null);
    }

    @Override
    public List<FormPlayer> getPlayers() {
        return server.getAllPlayers().stream().map(VelocityPlayer::new).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public Audience asAudience(CommandOrigin origin) {
        return ((CommandSource) origin.getHandle());
    }

    @Override
    public boolean isGeyserEnabled() {
        return server.getPluginManager().isLoaded("Geyser-Velocity");
    }

    @Override
    public boolean isFloodgateEnabled() {
        return server.getPluginManager().isLoaded("floodgate");
    }

    @Override
    public void registerPermission(String key, @Nullable String description, PermissionDefault def) {
        if (def != PermissionDefault.FALSE) {
            Logger.getLogger().warn("Registering permissions is currently not supported on Velocity! Remove permission default settings in configs to stop attempting.");
        }
    }

    @Override
    public void unregisterPermission(String key) {
        Logger.getLogger().debug("Not unregistered permission because registering is currently unsupported on Velocity");
    }

    @Override
    public void dispatchCommand(DispatchableCommand command) {
        commandManager.executeAsync(server.getConsoleCommandSource(), command.getCommand());
    }

    @Override
    public void dispatchCommand(UUID uuid, DispatchableCommand command) {
        Player player = server.getPlayer(uuid).orElse(null);
        if (player == null) {
            throw new IllegalArgumentException("Failed to find a player with the following UUID: " + uuid);
        }
        if (command.isPlayer()) {
            if (command.isOp()) {
                // todo: op commands on velocity
                Logger.getLogger().warn("Not executing [" + command.getCommand() + "] as operator because it isn't currently supported on velocity!");
            }
            commandManager.executeAsync(player, command.getCommand());
        } else {
            dispatchCommand(command);
        }
    }

    @Subscribe
    public void onCommandExecute(CommandExecuteEvent event) {
        CommandSource source = event.getCommandSource();
        if (!event.getResult().isAllowed() || !(source instanceof Player)) {
            return;
        }

        String name = COMMAND_PATTERN.split(event.getCommand().substring(1))[0]; // remove command slash and get first command
        Logger.getLogger().debug("preprocess command: [" + event.getCommand() + "] -> [" + name + "]");
        CustomCommand command = proxyCommands.get(name);
        if (command != null) {
            Player player = (Player) source;
            CommandType type = command.getMethod();
            if (player.hasPermission(command.getPermission())) {
                command.run(
                        new VelocityPlayer(player),
                        CrossplatForms.getInstance().getInterfaceManager(),
                        CrossplatForms.getInstance().getBedrockHandler()
                );
            } else if (type == CommandType.INTERCEPT_CANCEL) {
                player.sendMessage(CrossplatForms.LEGACY_SERIALIZER.deserialize(PERMISSION_MESSAGE));
            }

            if (type == CommandType.INTERCEPT_CANCEL) {
                event.setResult(CommandExecuteEvent.CommandResult.denied()); // todo: if this sends a message about denial we might have to replace the common with a dummy
            }
        }
    }
}
