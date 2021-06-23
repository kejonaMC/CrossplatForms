package dev.projectg.geyserhub.module.menu;

import dev.projectg.geyserhub.SelectorLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CommandUtils {

    public static final String playerPrefix = "player;";
    public static final String consolePrefix = "console;";

    /**
     * Process a prefixed command and run it
     * @param prefixedCommand A command that is prefixed with "player;" to run the command as the player, or "console;", to run the command as the console.
     * @param player the Player to run the command as, if prefixed with "player;"
     */
    public static void runCommand(@Nonnull String prefixedCommand, @Nonnull Player player) {
        CommandSender sender = Bukkit.getServer().getConsoleSender();
        if (prefixedCommand.startsWith(playerPrefix)) {
            sender = player;
        }
        // Split the input into two strings between ";" and get the second string
        String executableCommand = prefixedCommand.split(";", 2)[1];
        SelectorLogger.getLogger().debug("Running command: " + executableCommand + " as " + sender.getName());
        Bukkit.getServer().dispatchCommand(sender, executableCommand);
    }
}
