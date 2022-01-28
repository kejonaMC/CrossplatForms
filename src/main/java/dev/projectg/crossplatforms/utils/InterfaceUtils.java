package dev.projectg.crossplatforms.utils;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class InterfaceUtils {

    public static final String PLAYER_PREFIX = "player;";
    public static final String CONSOLE_PREFIX = "console;";

    private InterfaceUtils() {
    }

    /**
     * Sends a given form, identified by its name, to a BE or JE player.
     * If the form does not exist for their platform, they will be sent a message.
     * If forms are disabled on their platform, they will be sent a message.
     * @param player The {@link Player} to send the form to
     * @param bedrockRegistry The registry to pull bedrock forms from
     * @param javaMenuRegistry The registry to pull java inventory GUIs from
     * @param formName The name of the form to open
     */
    public static void sendInterface(@Nonnull Player player, @Nonnull BedrockFormRegistry bedrockRegistry, @Nonnull JavaMenuRegistry javaMenuRegistry, @Nonnull String formName) {
        if (CrossplatForms.getInstance().getBedrockHandler().isBedrockPlayer(player.getUniqueId())) {
            if (bedrockRegistry.isEnabled()) {
                BedrockForm form = bedrockRegistry.getForm(formName);
                if (form == null) {
                    player.sendMessage("[CForms] " + ChatColor.RED + "Sorry, the form " + formName + " doesn't exist! Specify a form with '/ghub form <form>'");
                } else {
                    form.sendForm(player.getUniqueId());
                }
            } else {
                player.sendMessage("[CForms] " + ChatColor.RED + "Sorry, Bedrock forms are disabled!");
            }
        } else {
            if (javaMenuRegistry.isEnabled()) {
                JavaMenu menu = javaMenuRegistry.getMenu(formName);
                if (menu == null) {
                    player.sendMessage("[CForms] " + ChatColor.RED + "Sorry, the menu " + formName + " doesn't exist! Specify a form with '/ghub form <form>'");
                } else {
                    menu.sendMenu(player);
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
    public static void runCommand(@Nonnull String command, @Nullable Player player) {
        Objects.requireNonNull(command);

        // Run as console by default
        CommandSender sender;
        if (command.startsWith(PLAYER_PREFIX)) {
            if (player == null) {
                throw new IllegalArgumentException("The following command is denoted to be run by a player, but a null player was passed internally: " + command);
            } else {
                sender = player;
            }
        } else {
            sender = Bukkit.getServer().getConsoleSender();
        }

        String executableCommand;
        if (command.startsWith(PLAYER_PREFIX) || command.startsWith(CONSOLE_PREFIX)) {
            // Split the input into two strings between ";" and get the second string
             executableCommand = command.split(";", 2)[1].trim();
        } else {
            executableCommand = command;
        }

        Logger.getLogger().debug("Running command: [" + executableCommand + "] as " + sender.getName());
        Bukkit.getServer().dispatchCommand(sender, executableCommand);
    }
}
