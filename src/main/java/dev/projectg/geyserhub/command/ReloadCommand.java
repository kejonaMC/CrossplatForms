package dev.projectg.geyserhub.command;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.Reloadable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class ReloadCommand implements CommandExecutor {
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull  Command command, @Nonnull String label, String[] args) {

        if (sender instanceof Player || sender instanceof ConsoleCommandSender) {

            if (GeyserHubMain.getInstance().loadConfiguration()) {
                sender.sendMessage("[GeyserHub] " + ChatColor.RED + "Failed to reload the configuration!");
                return true;
            }

            for (Reloadable reloadable : Reloadable.reloadables) {
                if (!reloadable.reload()) {
                    sender.sendMessage("[GeyserHub] " + ChatColor.RED + "Failed to reload class: " + ChatColor.RESET + reloadable.getClass().toString());
                }
            }
        }
        return true;
    }
}
