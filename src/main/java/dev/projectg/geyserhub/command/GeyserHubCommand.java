package dev.projectg.geyserhub.command;

import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.module.menu.java.JavaMenuRegistry;
import dev.projectg.geyserhub.reloadable.ReloadableRegistry;
import dev.projectg.geyserhub.module.menu.bedrock.BedrockFormRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

public class GeyserHubCommand implements CommandExecutor {

    private static final String[] HELP = {
            "/ghub - Opens the default form if one exists. If not, shows the help page",
            "/ghub - Opens the help page",
            "/ghub form <form> - Open a form with the defined name",
            "/ghub form <form> <player> - Sends a form to a given player",
            "/ghub reload - reloads the selector"
    };

    private static final String NO_PERMISSION = "Sorry, you don't have permission to run that command!";
    private static final String UNKNOWN = "Sorry, that's an unknown command!";

    private final BedrockFormRegistry bedrockRegistry;
    private final JavaMenuRegistry javaMenuRegistry;

    public GeyserHubCommand(BedrockFormRegistry bedrockRegistry, JavaMenuRegistry javaMenuRegistry) {
        this.bedrockRegistry = bedrockRegistry;
        this.javaMenuRegistry = javaMenuRegistry;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player || commandSender instanceof ConsoleCommandSender)) {
            return false;
        }
        // todo: cleanup
        if (args.length == 0) {
            // send the default form, help if console
            sendForm(commandSender, BedrockFormRegistry.DEFAULT);
            return true;
        }

        // At least one arg
        switch (args[0]) {
            case "reload":
                if (commandSender.hasPermission("geyserhub.reload")) {
                    if (ReloadableRegistry.reloadAll()) {
                        sendMessage(commandSender, SelectorLogger.Level.INFO, "Successfully reloaded.");
                    } else {
                        sendMessage(commandSender, SelectorLogger.Level.SEVERE, "There was an error reloading something! Please check the server console for further information.");
                    }
                } else {
                    sendMessage(commandSender, SelectorLogger.Level.SEVERE, NO_PERMISSION);
                }
                break;
            case "help":
                sendHelp(commandSender);
                break;
            case "form":
                if (commandSender.hasPermission("geyserhub.form")) {
                    if (args.length == 1) {
                        sendMessage(commandSender, SelectorLogger.Level.SEVERE, "Please specify a form to open! Specify a form with \"/ghub form <form>\"");
                    } else if (args.length == 2) {
                        sendForm(commandSender, args[1]);
                    } else if (args.length == 3) {
                        if (commandSender.hasPermission("geyserhub.form.others")) {
                            Player target = Bukkit.getServer().getPlayer(args[2]);
                            if (target == null) {
                                sendMessage(commandSender, SelectorLogger.Level.SEVERE, "That player doesn't exist!");
                            } else {
                                sendForm(target, args[1]);
                                sendMessage(commandSender, SelectorLogger.Level.INFO, "Made " + target.getName() + " open form: " + args[1]);
                            }
                        } else {
                            sendMessage(commandSender, SelectorLogger.Level.SEVERE, NO_PERMISSION);
                        }
                    } else {
                        sendMessage(commandSender, SelectorLogger.Level.SEVERE, "Too many command arguments!");
                    }
                } else {
                    sendMessage(commandSender, SelectorLogger.Level.SEVERE, NO_PERMISSION);
                }
                break;
            default:
                sendMessage(commandSender, SelectorLogger.Level.SEVERE, UNKNOWN);
                break;
        }
        return true;
    }

    private void sendHelp(CommandSender commandSender) {
        // todo: only show players with the given permissions certain entries? not sure if it can be integrated any way into spigot command completions
        // todo: check if these are sent on consecutive lines or the same one :(
        commandSender.sendMessage(HELP);
    }

    /**
     * send a form to a command sender. if the commandsender is a console then it will just send the help page.
     * @param commandSender the command sender.
     * @param formName the form name to send
     */
    private void sendForm(@Nonnull CommandSender commandSender, @Nonnull String formName) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            UUID uuid = player.getUniqueId();
            if (FloodgateApi.getInstance().isFloodgatePlayer(uuid)) {
                if (bedrockRegistry.isEnabled()) {
                    if (bedrockRegistry.getFormNames().contains(formName)) {
                        bedrockRegistry.sendForm(FloodgateApi.getInstance().getPlayer(uuid), formName);
                    } else {
                        sendMessage(player, SelectorLogger.Level.SEVERE, "Sorry, that form doesn't exist! Specify a form with \"/ghub form <form>\"");
                    }
                } else {
                    sendMessage(player, SelectorLogger.Level.SEVERE, "Sorry, Bedrock forms are disabled!");
                }
            } else {
                if (javaMenuRegistry.isEnabled()) {
                    if (javaMenuRegistry.getFormNames().contains(formName)) {
                        javaMenuRegistry.sendForm(player, formName);
                    } else {
                        sendMessage(player, SelectorLogger.Level.SEVERE, "Sorry, that form doesn't exist! Specify a form with \"/ghub form <form>\"");
                    }
                } else {
                    sendMessage(player, SelectorLogger.Level.SEVERE, "Sorry, Java menus are disabled!");
                }
            }
        } else if (commandSender instanceof ConsoleCommandSender) {
            sendHelp(commandSender);
        }
    }

    private void sendMessage(@Nonnull CommandSender sender, @Nonnull SelectorLogger.Level level, @Nonnull String message) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(level);
        Objects.requireNonNull(message);

        if (sender instanceof ConsoleCommandSender) {
            SelectorLogger.getLogger().log(level, message);
        } else {
            ChatColor colour;
            switch (level) {
                default: // intentional fallthrough
                case INFO:
                    colour = ChatColor.RESET;
                    break;
                case WARN:
                    colour = ChatColor.GOLD;
                    break;
                case SEVERE:
                    colour = ChatColor.RED;
                    break;
            }
            sender.sendMessage("[GeyserHub] " + colour + message);
        }
    }
}
