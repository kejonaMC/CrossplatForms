package dev.projectg.geyserhub.module.menu;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.utils.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MenuUtils {

    public static final String playerPrefix = "player;";
    public static final String consolePrefix = "console;";

    /**
     * @param commands Commands list, an empty list can be passed for no commands.
     * @param serverName The server name, can passed as null for no server.
     * @param player the Player to run everything on.
     */
    public static void affectPlayer(@Nonnull List<String> commands, @Nullable String serverName, @Nonnull Player player) {
        Objects.requireNonNull(commands);
        Objects.requireNonNull(player);

        if (!commands.isEmpty()) {
            // Get the commands from the list of commands and replace any playerName placeholders
            for (String command : commands) {
                MenuUtils.runCommand(PlaceholderUtils.setPlaceholders(player, command), player);
            }
        }

        if (serverName != null) {
            // This should never be out of bounds considering its size is the number of valid buttons
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(baos)) {
                out.writeUTF("Connect");
                out.writeUTF(serverName);
                player.sendPluginMessage(GeyserHubMain.getInstance(), "BungeeCord", baos.toByteArray());
                player.sendMessage(ChatColor.DARK_AQUA + "Trying to send you to: " + ChatColor.GREEN + serverName);
            } catch (IOException e) {
                SelectorLogger.getLogger().severe("Failed to send a plugin message to Bungeecord!");
                e.printStackTrace();
            }
        }
    }



    /**
     * Process a prefixed command and run it
     * @param prefixedCommand A command that is prefixed with "player;" to run the command as the player, or "console;", to run the command as the console.
     * @param player the Player to run the command as, if prefixed with "player;"
     */
    public static void runCommand(@Nonnull String prefixedCommand, @Nonnull Player player) {
        Objects.requireNonNull(prefixedCommand);
        Objects.requireNonNull(player);

        CommandSender sender = Bukkit.getServer().getConsoleSender();
        if (prefixedCommand.startsWith(playerPrefix)) {
            sender = player;
        }
        // Split the input into two strings between ";" and get the second string
        String executableCommand = prefixedCommand.split(";", 2)[1].stripLeading();
        SelectorLogger.getLogger().debug("Running command: [" + executableCommand + "] as " + sender.getName());
        Bukkit.getServer().dispatchCommand(sender, executableCommand);
    }

    /**
     * Gets the commands from a config section with a "Commands" string list.
     * @param buttonData the config section with the string list
     * @return the commands. will return an empty list in case of failure, or if the list was empty.
     */
    @Nonnull
    public static List<String> getCommands(@Nonnull ConfigurationSection buttonData) {
        Objects.requireNonNull(buttonData);
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

    /**
     * Get the server name from a button configuration section
     * @param buttonData the config section
     * @return the server name, null if there was no server
     */
    @Nullable
    public static String getServer(@Nonnull ConfigurationSection buttonData) {
        Objects.requireNonNull(buttonData);
        SelectorLogger logger = SelectorLogger.getLogger();

        if (buttonData.contains("Server") && buttonData.isString("Server")) {
            String serverName = Objects.requireNonNull(buttonData.getString("Server"));
            logger.debug(getParentName(buttonData) + "." + buttonData.getName() + " contains BungeeCord target server: " + serverName);
            return serverName;
        }
        return null;
    }

    /**
     * Get the name of the parent config section of the given config section
     * @param configSection the config section to get the parent name of
     * @return the parent name, "null" if there was no parent
     */
    @Nonnull
    public static String getParentName(@Nonnull ConfigurationSection configSection) {
        Objects.requireNonNull(configSection);

        ConfigurationSection parent = configSection.getParent();
        if (parent == null) {
            return "null";
        } else {
            return parent.getName();
        }
    }
}
