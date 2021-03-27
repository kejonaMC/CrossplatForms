package com.alysaa.serverselector.command;

import com.alysaa.serverselector.GServerSelector;
import com.alysaa.serverselector.SelectorForm;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

public class SelectorCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("servers") && player.hasPermission("gserverselector.servers")) {
                try {
                    SelectorForm.SelectServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            GServerSelector.getLogger.info
            return false;
        }
    }
}
