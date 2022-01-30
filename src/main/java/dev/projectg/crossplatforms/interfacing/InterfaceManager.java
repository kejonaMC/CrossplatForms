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
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class InterfaceManager {

    public static final String PLAYER_PREFIX = "player;";
    public static final String CONSOLE_PREFIX = "console;";

    private final ServerHandler serverHandler;
    private final BedrockHandler bedrockHandler;

    @Getter
    private final BedrockFormRegistry bedrockRegistry;

    @Getter
    private final JavaMenuRegistry javaRegistry;


    /**
     * Sends a given form, identified by its name, to a BE or JE player.
     * If the form does not exist for their platform, they will be sent a message.
     * If forms are disabled on their platform, they will be sent a message.
     * @param player The {@link Player} to send the form to
     * @param formName The name of the form to open
     */
    public void sendInterface(@Nonnull Player player, @Nonnull String formName) {
        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            if (bedrockRegistry.isEnabled()) {
                BedrockForm form = bedrockRegistry.getForm(formName);
                if (form == null) {
                    player.sendMessage("[CForms] " + ChatColor.RED + "Sorry, the form " + formName + " doesn't exist! Specify a form with '/ghub form <form>'");
                } else {
                    form.sendForm(player.getUuid(), this);
                }
            } else {
                player.sendMessage("[CForms] " + ChatColor.RED + "Sorry, Bedrock forms are disabled!");
            }
        } else {
            if (javaRegistry.isEnabled()) {
                JavaMenu menu = javaRegistry.getMenu(formName);
                if (menu == null) {
                    player.sendMessage("[CForms] " + ChatColor.RED + "Sorry, the menu " + formName + " doesn't exist! Specify a form with '/ghub form <form>'");
                } else {
                    menu.sendMenu((org.bukkit.entity.Player) player.getHandle());
                }
            } else {
                player.sendMessage("[CForms] " + ChatColor.RED + "Sorry, Java menus are disabled!");
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
