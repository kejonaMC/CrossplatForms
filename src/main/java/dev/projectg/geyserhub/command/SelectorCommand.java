package dev.projectg.geyserhub.command;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.menu.BedrockMenu;
import dev.projectg.geyserhub.menu.JavaMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import javax.annotation.Nonnull;

public class SelectorCommand implements CommandExecutor {
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull  Command command, @Nonnull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                if (BedrockMenu.getInstance().isEnabled()){
                    BedrockMenu.getInstance().sendForm(FloodgateApi.getInstance().getPlayer(player.getUniqueId()));
                } else {
                    player.sendMessage("[GeyserHub] " + ChatColor.RED + "Sorry, selector form is not available to Bedrock players!");
                }
            } else {
                if (JavaMenu.isEnabled()) {
                    JavaMenu.openMenu(player, GeyserHubMain.getInstance().getConfig());
                } else {
                    player.sendMessage("[GeyserHub] " + ChatColor.RED + "Sorry, selector form is not available to Java players!");
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            SelectorLogger.getLogger().warn("This command only works in-game!");
        }
        return true;
    }
}
