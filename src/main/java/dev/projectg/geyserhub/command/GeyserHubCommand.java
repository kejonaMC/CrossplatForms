package dev.projectg.geyserhub.command;

import dev.projectg.geyserhub.config.ConfigId;
import dev.projectg.geyserhub.reloadable.ReloadableRegistry;
import dev.projectg.geyserhub.module.menu.bedrock.BedrockFormRegistry;
import dev.projectg.geyserhub.module.menu.java.JavaMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.UUID;

public class GeyserHubCommand implements CommandExecutor {

    private static final String[] HELP = {
            "/ghub - Opens the default form if one exists. If not, shows the help page",
            "/ghub - Opens the help page",
            "/ghub <form> - Sends the player a selector with the defined name",
            "/ghub reload - reloads the selector"
    };

    private static final String NO_PERMISSION = "[GeyserHub] Sorry, you don't have permission to run that command!";
    private static final String UNKNOWN = "[GeyserHub] Sorry, that's an unknown command!";

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
                        commandSender.sendMessage("[GeyserHub] Successfully reloaded.");
                    } else {
                        commandSender.sendMessage("[GeyserHub] There was an error reloading something! Please check the server console for further information.");
                    }
                } else {
                    commandSender.sendMessage(NO_PERMISSION);
                }
                break;
            case "help":
                sendHelp(commandSender);
                break;
            case "form":
                if (commandSender.hasPermission("geyserhub.form")) {
                    if (args.length == 1) {
                        commandSender.sendMessage("[GeyserHub] Please specify a form to open! Specify a form with \"/ghub form <form>\"");
                    } else if (args.length > 2) {
                        commandSender.sendMessage("[GeyserHub] This command only takes one argument!");
                    } else {
                        sendForm(commandSender, args[1]);
                    }
                } else {
                    commandSender.sendMessage(NO_PERMISSION);
                }
                break;
            default:
                commandSender.sendMessage(UNKNOWN);
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
                if (BedrockFormRegistry.getInstance().isEnabled()) {
                    if (BedrockFormRegistry.getInstance().getFormNames().contains(formName)) {
                        BedrockFormRegistry.getInstance().sendForm(FloodgateApi.getInstance().getPlayer(uuid), formName);
                    } else {
                        player.sendMessage("Sorry, that form doesn't exist! Specify a form with \"/ghub form <form>\"");
                    }
                } else {
                    player.sendMessage("Sorry, Bedrock forms are disabled!");
                }
            } else {
                JavaMenu.openMenu(player, ConfigId.SELECTOR);
            }
        } else if (commandSender instanceof ConsoleCommandSender) {
            sendHelp(commandSender);
        }
    }
}
