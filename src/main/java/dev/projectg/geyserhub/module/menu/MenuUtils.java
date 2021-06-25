package dev.projectg.geyserhub.module.menu;

import dev.projectg.geyserhub.SelectorLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MenuUtils {

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
        String executableCommand = prefixedCommand.split(";", 2)[1].stripLeading();
        SelectorLogger.getLogger().debug("Running command: [" + executableCommand + "] as " + sender.getName());
        Bukkit.getServer().dispatchCommand(sender, executableCommand);
    }

    @Nonnull
    public static List<String> getCommands(@Nonnull ConfigurationSection buttonData) {
        SelectorLogger logger = SelectorLogger.getLogger();

        if (buttonData.contains("Commands") && buttonData.isList("Commands")) {
            if (buttonData.getStringList("Commands").isEmpty()) {
                logger.warn(getParentName(buttonData) + "." + buttonData.getName() + " contains commands list but the list was empty.");
            } else {
                List<String> commands = buttonData.getStringList("Commands");
                logger.debug(getParentName(buttonData) + "." + buttonData.getName() + " contains commands: " + commands);
                return commands;
            }
        }
        return Collections.emptyList();
    }

    @Nullable
    public static String getServer(@Nonnull ConfigurationSection buttonData) {
        SelectorLogger logger = SelectorLogger.getLogger();

        if (buttonData.contains("Server") && buttonData.isString("Server")) {
            String serverName = Objects.requireNonNull(buttonData.getString("Server"));
            logger.debug(getParentName(buttonData) + "." + buttonData.getName() + " contains BungeeCord target server: " + serverName);
            return serverName;
        }
        return null;
    }

    @Nonnull
    public static String getParentName(@Nonnull ConfigurationSection configSection) {
        ConfigurationSection parent = configSection.getParent();
        if (parent == null) {
            return "null";
        } else {
            return parent.getName();
        }
    }
}
