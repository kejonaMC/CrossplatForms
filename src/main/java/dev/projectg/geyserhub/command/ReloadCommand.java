package dev.projectg.geyserhub.command;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.bedrockmenu.BedrockMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player || sender instanceof ConsoleCommandSender) {

            GeyserHubMain.getInstance().loadConfig();
            if (BedrockMenu.init(GeyserHubMain.getInstance().getConfig())) {
                sender.sendMessage("[GeyserHubMain]" + ChatColor.GREEN + "Reloaded the server selector form.");
            } else {
                sender.sendMessage("[GeyserHubMain]" + ChatColor.RED + "Failed to reload the server selector form!");
            }
        }
        return true;
    }
}
