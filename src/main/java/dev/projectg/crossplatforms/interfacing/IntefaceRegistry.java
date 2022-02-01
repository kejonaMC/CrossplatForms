package dev.projectg.crossplatforms.interfacing;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.Player;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class IntefaceRegistry {

    public static final String PLAYER_PREFIX = "player;";
    public static final String CONSOLE_PREFIX = "console;";

    private final ServerHandler serverHandler;
    private final BedrockHandler bedrockHandler;

    @Getter
    private final BedrockFormRegistry bedrockRegistry;

    @Getter
    private final JavaMenuRegistry javaRegistry;

    // todo: method to get an interface by name

    /**
     * Sends a given form, identified by its name, to a BE or JE player.
     * If the form does not exist for their platform, they will be sent a message.
     * If forms are disabled on their platform, they will be sent a message.
     * @param player The {@link Player} to send the form to
     * @param id The name of the form or menu to open
     */
    public void sendInterface(@Nonnull Player player, @Nonnull String id) {
        UUID uuid = player.getUuid();

        BedrockForm form = bedrockRegistry.getForm(id);
        JavaMenu menu = javaRegistry.getMenu(id);

        // todo: fml... interface class needs an abstract send(Player) method
        if (bedrockHandler.isBedrockPlayer(uuid)) {
            if (form != null) {
                // form exists
                if (player.hasPermission(form.permission(Interface.Limit.USE))) {
                    form.sendForm(uuid, this);
                } else {
                    player.sendMessage("You don't have permission to use that form.");
                }
            } else if (menu != null) {
                if (menu.isAllowBedrock()) {
                    // menu exists and BE is allowed
                    if (player.hasPermission(menu.permission(Interface.Limit.USE))) {
                        menu.sendMenu((org.bukkit.entity.Player) player.getHandle());
                    } else {
                        player.sendMessage("You don't have permission to use that menu.");
                    }
                } else {
                    // menu exists and BE is not allowed
                    player.sendMessage("That menu is only available to Java Edition players.");
                }
            } else {
                player.sendMessage("The form or menu '" + id + "' doesn't exist.");
            }
        } else {
            if (menu != null) {
                if (player.hasPermission(menu.permission(Interface.Limit.USE))) {
                    menu.sendMenu((org.bukkit.entity.Player) player.getHandle());
                } else {
                    player.sendMessage("You don't have permission to use that menu.");
                }
            } else {
                player.sendMessage("The menu '" + id + "' doesn't exist.");
            }
        }
    }

    /**
     * Process a command and run it.
     * If the command is prefixed with "player;" the command will be run as the player given, which CANNOT be null.
     * If the command is prefixed with "console;" the command will be run as the console.
     *
     * @param command The command to run
     * @param player the Player to run the command as, if prefixed with "player;"
     */
    public void runCommand(@Nonnull String command, @Nonnull UUID player) {
        Objects.requireNonNull(command);

        String executableCommand;
        if (command.startsWith(PLAYER_PREFIX) || command.startsWith(CONSOLE_PREFIX)) {
            // Split the input into two strings between ";" and get the second string
            executableCommand = command.split(";", 2)[1].trim();
        } else {
            executableCommand = command;
        }

        if (command.startsWith(PLAYER_PREFIX)) {
            Logger.getLogger().debug("Running command: [" + executableCommand + "] as " + player);
            serverHandler.dispatchCommand(player, executableCommand);
        } else {
            Logger.getLogger().debug("Running command: [" + executableCommand + "] as console");
            serverHandler.dispatchCommand(executableCommand);
        }
    }
}
