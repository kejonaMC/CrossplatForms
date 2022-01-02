package dev.projectg.crossplatforms.form;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.form.java.JavaMenu;
import dev.projectg.crossplatforms.form.bedrock.BedrockForm;
import dev.projectg.crossplatforms.form.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.form.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.utils.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MenuUtils {

    public static final String PLAYER_PREFIX = "player;";
    public static final String CONSOLE_PREFIX = "console;";

    /**
     * Sends a given form, identified by its name, to a BE or JE player.
     * If the form does not exist for their platform, they will be sent a message.
     * If forms are disabled on their platform, they will be sent a message.
     * @param player The {@link Player} to send the form to
     * @param bedrockRegistry The registry to pull bedrock forms from
     * @param javaMenuRegistry The registry to pull java inventory GUIs from
     * @param formName The name of the form to open
     */
    public static void sendForm(@Nonnull Player player, @Nonnull BedrockFormRegistry bedrockRegistry, @Nonnull JavaMenuRegistry javaMenuRegistry, @Nonnull String formName) {
        if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            if (bedrockRegistry.isEnabled()) {
                BedrockForm form = bedrockRegistry.getMenu(formName);
                if (form == null) {
                    player.sendMessage("[GeyserHub] " + ChatColor.RED + "Sorry, that form doesn't exist! Specify a form with '/ghub form <form>'");
                } else {
                    form.sendForm(FloodgateApi.getInstance().getPlayer(player.getUniqueId()));
                }
            } else {
                player.sendMessage("[GeyserHub] " + ChatColor.RED + "Sorry, Bedrock forms are disabled!");
            }
        } else {
            if (javaMenuRegistry.isEnabled()) {
                JavaMenu menu = javaMenuRegistry.getMenu(formName);
                if (menu == null) {
                    player.sendMessage("[GeyserHub] " + ChatColor.RED + "Sorry, that form doesn't exist! Specify a form with '/ghub form <form>'");
                } else {
                    menu.sendMenu(player);
                }
            } else {
                player.sendMessage("[GeyserHub] " + ChatColor.RED + "Sorry, Java menus are disabled!");
            }
        }

    }

    /**
     * @param commands Commands list, an empty list can be passed for no commands.
     * @param serverName The server name, can passed as null for no server.
     * @param player the Player to run everything on.
     */
    public static void affectPlayer(@Nonnull List<String> commands, @Nullable String serverName, @Nonnull Player player) {
        Objects.requireNonNull(commands);
        Objects.requireNonNull(player);
        FileConfiguration config = CrossplatForms.getInstance().getConfigManager().getFileConfiguration(ConfigId.MAIN);
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
                player.sendPluginMessage(CrossplatForms.getInstance(), "BungeeCord", baos.toByteArray());
                String message = config.getString("Bungeecord-Message", "");
                if (!message.isEmpty()) {
                    player.sendMessage(PlaceholderUtils.setPlaceholders(player, message).replace("%server%", serverName));
                }
            } catch (IOException e) {
                Logger.getLogger().severe("Failed to send a plugin message to Bungeecord!");
                e.printStackTrace();
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
        CommandSender sender = Bukkit.getServer().getConsoleSender();
        if (command.startsWith(PLAYER_PREFIX)) {
            if (player == null) {
                throw new IllegalArgumentException("The following command is denoted to be run by a player, but a null player was passed internally: " + command);
            } else {
                sender = player;
            }
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

    /**
     * Gets the commands from a config section with a "Commands" string list.
     * @param buttonData the config section with the string list
     * @return the commands. will return an empty list in case of failure, or if the list was empty.
     */
    @Nonnull
    public static List<String> getCommands(@Nonnull ConfigurationSection buttonData) {
        Objects.requireNonNull(buttonData);
        Logger logger = Logger.getLogger();

        if (buttonData.contains("Commands", true) && buttonData.isList("Commands")) {
            List<String> commands = buttonData.getStringList("Commands");
            if (commands.isEmpty()) {
                logger.warn(getParentName(buttonData) + "." + buttonData.getName() + " contains commands list but the list was empty.");
            } else {
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

        if (buttonData.contains("Server", true) && buttonData.isString("Server")) {
            return Objects.requireNonNull(buttonData.getString("Server"));
        }
        return null;
    }

    /**
     * Get the name of the parent of a config section
     * @param configSection the config section
     * @return the parent name, "null" if there was no parent, or if the the given configSection was null
     */
    @Nonnull
    public static String getParentName(@Nullable ConfigurationSection configSection) {
        if (configSection != null) {
            ConfigurationSection parent = configSection.getParent();
            if (parent != null) {
                return parent.getName();
            }
        }
        return "null";
    }
}
