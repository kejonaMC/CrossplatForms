package com.alysaa.serverselector.command;

import com.alysaa.serverselector.GServerSelector;
import com.alysaa.serverselector.form.SelectorForm;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class ReloadCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof ConsoleCommandSender) {
            Logger logger = GServerSelector.getInstance().getLogger();
            if (SelectorForm.init()) {
                logger.info("Reload the server selector form.");
            } else {
                logger.warning("Failed to reload the server selector form!");
            }
        } else if (sender instanceof Player){
            sender.sendMessage(ChatColor.RED + "This command only works in-game!");
        }
        return true;
    }
}
