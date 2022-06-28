package dev.kejona.crossplatforms.velocity.handler;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.CommandType;
import dev.kejona.crossplatforms.command.DispatchableCommand;
import dev.kejona.crossplatforms.command.custom.InterceptCommand;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.proxy.PermissionHook;
import dev.kejona.crossplatforms.proxy.ProxyHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class VelocityHandler extends ProxyHandler implements ServerHandler {

    private final ProxyServer server;
    private final CommandManager commandManager;
    private final ConsoleCommandSource console;

    public VelocityHandler(ProxyServer server, PermissionHook permissionHook) {
        super(permissionHook);
        this.server = server;
        this.commandManager = server.getCommandManager();
        this.console = server.getConsoleCommandSource();
    }

    private Player getPlayerOrThrow(UUID uuid) {
        return server.getPlayer(uuid).orElseThrow(() -> new IllegalArgumentException("Failed to find a player with the following UUID: " + uuid));
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
    public Stream<FormPlayer> getPlayers() {
        return server.getAllPlayers().stream().map(VelocityPlayer::new);
    }

    @Nonnull
    @Override
    public Audience asAudience(CommandOrigin origin) {
        return ((CommandSource) origin.getHandle());
    }

    @Override
    public boolean isGeyserEnabled() {
        return server.getPluginManager().isLoaded("geyser");
    }

    @Override
    public boolean isFloodgateEnabled() {
        return server.getPluginManager().isLoaded("floodgate");
    }

    @Override
    public void dispatchCommand(DispatchableCommand command) {
        dispatchCommand(console, command.getCommand());
    }

    @Override
    public void dispatchCommand(UUID uuid, DispatchableCommand command) {
        dispatchCommand(getPlayerOrThrow(uuid), command);
    }

    @Override
    public void dispatchCommands(UUID uuid, List<DispatchableCommand> commands) {
        Player player = getPlayerOrThrow(uuid);
        commands.forEach(c -> dispatchCommand(player, c));
    }

    private void dispatchCommand(Player player, DispatchableCommand command) {
        if (command.isPlayer()) {
            if (command.isOp()) {
                // todo: op commands on velocity
                Logger.get().warn("Not executing [" + command.getCommand() + "] as operator because it isn't currently supported on velocity!");
            }
            dispatchCommand(player, command.getCommand());
        } else {
            dispatchCommand(command);
        }
    }

    private void dispatchCommand(CommandSource source, String cmd) {
        commandManager.executeAsync(source, cmd).thenAccept(success -> {
            if (!success) {
                Logger.get().severe("Failed to run command '" + cmd + "' by sender: " + getName(source));
            }
        });
    }

    public String getName(CommandSource source)  {
        if (source instanceof ConsoleCommandSource) {
            // running console commands is probably more common
            return "console";
        } else {
            return source.getOrDefault(Identity.NAME, source.toString());
        }
    }

    @Subscribe
    public void onCommandExecute(CommandExecuteEvent event) {
        CommandSource source = event.getCommandSource();
        if (!event.getResult().isAllowed() || !(source instanceof Player)) {
            return;
        }

        String input = event.getCommand();
        Logger.get().debug("preprocess command: [" + event.getCommand() + "] -> [" + input + "]");
        InterceptCommand command = findCommand(input);
        if (command != null) {
            Player player = (Player) source;
            CommandType type = command.getMethod();
            BedrockHandler bedrockHandler = CrossplatForms.getInstance().getBedrockHandler();
            if (command.getPlatform().matches(player.getUniqueId(), bedrockHandler)) {
                String permission = command.getPermission();
                if (permission == null || player.hasPermission(permission)) {
                    command.run(new VelocityPlayer(player));

                    if (type == CommandType.INTERCEPT_CANCEL) {
                        event.setResult(CommandExecuteEvent.CommandResult.denied()); // todo: if this sends a message about denial we might have to replace the common with a dummy
                    }
                }
            }
        }
    }
}
