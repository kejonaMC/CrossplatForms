package dev.projectg.serverselector.command;

import dev.projectg.serverselector.SelectorLogger;
import dev.projectg.serverselector.form.SelectorForm;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof ConsoleCommandSender) {
            SelectorLogger logger = SelectorLogger.getLogger();
            if (SelectorForm.init()) {
                logger.info("Reloaded the server selector form.");
            } else {
                logger.severe("Failed to reload the server selector form!");
            }
        } else if (sender instanceof Player){
            Player player = (Player) sender;
            if (SelectorForm.init()) {
                player.sendMessage("[GServerSelector]" + ChatColor.GREEN + "Reloaded the server selector form.");
            } else {
                player.sendMessage("[GServerSelector]" + ChatColor.RED + "Failed to reload the server selector form!");
            }
        }
        return true;
    }
}
