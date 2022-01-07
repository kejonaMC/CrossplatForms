package dev.projectg.crossplatforms.command;

import com.google.common.collect.ImmutableMap;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.utils.InterfaceUtils;
import dev.projectg.crossplatforms.form.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;
import dev.projectg.crossplatforms.form.bedrock.BedrockFormRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;

public class MainCommand implements CommandExecutor {

    private static final Map<Logger.Level, ChatColor> LOGGER_COLORS = ImmutableMap.of(
            Logger.Level.INFO, ChatColor.RESET,
            Logger.Level.WARN, ChatColor.GOLD,
            Logger.Level.SEVERE, ChatColor.RED);

    private static final String[] HELP = {
            "/forms - Opens the default form if one exists. If not, shows the help page",
            "/forms help - Opens the help page",
            "/forms open <form> - Open a form with the defined name",
            "/forms open <form> <player> - Sends a form to a given player",
            "/forms reload - reloads the configurations"
    };

    private static final String NO_PERMISSION = "Sorry, you don't have permission to run that command!";
    private static final String UNKNOWN = "Sorry, that's an unknown command!";

    private final BedrockHandler bedrockHandler;
    private final BedrockFormRegistry bedrockFormRegistry;
    private final JavaMenuRegistry javaMenuRegistry;

    public MainCommand(BedrockHandler bedrockHandler, BedrockFormRegistry bedrockFormRegistry, JavaMenuRegistry javaMenuRegistry) {
        this.bedrockHandler = bedrockHandler;
        this.bedrockFormRegistry = bedrockFormRegistry;
        this.javaMenuRegistry = javaMenuRegistry;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player || commandSender instanceof ConsoleCommandSender)) {
            return false;
        }

        if (!commandSender.hasPermission(Permissions.BASE)) {
            sendMessage(commandSender, Logger.Level.SEVERE, NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            sendHelp(commandSender);
            return true;
        }

        // At least one arg
        switch (args[0]) {
            case "reload":
                if (commandSender.hasPermission(Permissions.RELOAD_COMMAND)) {
                    if (!ReloadableRegistry.reloadAll()) {
                        sendMessage(commandSender, Logger.Level.SEVERE, "There was an error reloading something! Please check the server console for further information.");
                    }
                } else {
                    deny(commandSender);
                }
                break;
            case "help":
                sendHelp(commandSender);
                break;
            case "version":
                if (commandSender.hasPermission(Permissions.VERSION_COMMAND)) {
                    sendMessage(commandSender, Logger.Level.INFO, "CrossplatForms version:");
                    sendMessage(commandSender, Logger.Level.INFO, "Branch: " + CrossplatForms.getInstance().getBranch() + ", Commit: " + CrossplatForms.getInstance().getCommit());
                } else {
                    deny(commandSender);
                }
                break;
            case "identify":
                if (args.length == 1) {
                    if (commandSender instanceof Player player) {
                        String message = bedrockHandler.isBedrockPlayer(player.getUniqueId()) ? "You are a bedrock player" : "You are not a bedrock player";
                        sendMessage(commandSender, Logger.Level.INFO, message);
                    } else {
                        sendMessage(commandSender, Logger.Level.SEVERE, "Specify a player to identify");
                    }
                } else if (args.length == 2) {
                    if (commandSender.hasPermission(Permissions.IDENTIFY_COMMAND)) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            sendMessage(commandSender, Logger.Level.SEVERE, "That player doesn't exist");
                        } else {
                            String message = args[1] + (bedrockHandler.isBedrockPlayer(target.getUniqueId()) ? " is a Bedrock player" : " is not a Bedrock player");
                            sendMessage(commandSender, Logger.Level.INFO, message);
                        }
                    } else {
                        deny(commandSender);
                    }
                } else {
                    sendMessage(commandSender, Logger.Level.SEVERE, "Too many arguments!");
                }
                break;
            case "open":
                if (commandSender.hasPermission(Permissions.OPEN_COMMAND)) {
                    if (args.length == 1) {
                        sendMessage(commandSender, Logger.Level.SEVERE, "Please specify a form to open! Specify a form with \"/forms form <form>\"");
                    } else if (args.length == 2) {
                        sendForm(commandSender, args[1]);
                    } else if (args.length == 3) {
                        if (commandSender.hasPermission(Permissions.OPEN_COMMAND_OTHER)) {
                            Player target = Bukkit.getServer().getPlayer(args[2]);
                            if (target == null) {
                                sendMessage(commandSender, Logger.Level.SEVERE, "That player doesn't exist!");
                            } else {
                                sendForm(target, args[1]);
                                sendMessage(commandSender, Logger.Level.INFO, "Made " + target.getName() + " open form: " + args[1]);
                            }
                        } else {
                            deny(commandSender);
                        }
                    } else {
                        sendMessage(commandSender, Logger.Level.SEVERE, "Too many command arguments!");
                    }
                } else {
                    deny(commandSender);
                }
                break;
            default:
                sendMessage(commandSender, Logger.Level.SEVERE, UNKNOWN);
                break;
        }

        return true;
    }

    private void deny(CommandSender commandSender) {
        sendMessage(commandSender, Logger.Level.SEVERE, NO_PERMISSION);
    }

    private void sendHelp(CommandSender commandSender) {
        // todo: only show players with the given permissions certain entries? not sure if it can be integrated any way into spigot command completions
        commandSender.sendMessage(HELP);
    }

    /**
     * send a form to a command sender. if the commandsender is a console then it will just send the help page.
     * @param commandSender the command sender.
     * @param formName the form name to send
     */
    private void sendForm(@Nonnull CommandSender commandSender, @Nonnull String formName) {
        if (commandSender instanceof Player) {
            InterfaceUtils.sendInterface((Player) commandSender, bedrockFormRegistry, javaMenuRegistry, formName);
        } else if (commandSender instanceof ConsoleCommandSender) {
            sendHelp(commandSender);
        }
    }

    public static void sendMessage(@Nonnull CommandSender sender, @Nonnull Logger.Level level, @Nonnull String message) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(level);
        Objects.requireNonNull(message);

        if (sender instanceof ConsoleCommandSender) {
            Logger.getLogger().log(level, message);
        } else {
            sender.sendMessage("[CrossplatForms] " + LOGGER_COLORS.getOrDefault(level, ChatColor.RESET) + message);
        }
    }
}
