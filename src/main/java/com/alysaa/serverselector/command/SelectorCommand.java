package com.alysaa.serverselector.command;

import com.alysaa.serverselector.GServerSelector;
import com.alysaa.serverselector.form.SelectorForm;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;


public class SelectorCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("servers") && player.hasPermission("gserverselector.servers")) {
                try {
                    SelectorForm.sendSelector(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            GServerSelector.getInstance().getLogger().info("This command only works in-game!");
        }
        return false;
    }
}
