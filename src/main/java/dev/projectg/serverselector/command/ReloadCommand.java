package dev.projectg.serverselector.command;

import dev.projectg.serverselector.GServerSelector;
import dev.projectg.serverselector.form.SelectorForm;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player || sender instanceof ConsoleCommandSender) {

            GServerSelector.getInstance().loadConfig();
            if (SelectorForm.init(GServerSelector.getInstance().getConfig())) {
                sender.sendMessage("[GServerSelector]" + ChatColor.GREEN + "Reloaded the server selector form.");
            } else {
                sender.sendMessage("[GServerSelector]" + ChatColor.RED + "Failed to reload the server selector form!");
            }
        }
        return true;
    }
}
