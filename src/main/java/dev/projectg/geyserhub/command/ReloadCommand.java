package dev.projectg.geyserhub.command;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.Reloadable;
import dev.projectg.geyserhub.ReloadableRegistry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ReloadCommand implements CommandExecutor {
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull  Command command, @Nonnull String label, String[] args) {

        if (sender instanceof Player || sender instanceof ConsoleCommandSender) {

            if (GeyserHubMain.getInstance().loadConfiguration()) {
                sender.sendMessage("[GeyserHub] Reloaded the configuration, reloading modules...");
            } else {
                sender.sendMessage("[GeyserHub] " + ChatColor.RED + "Failed to reload the configuration!");
                return true;
            }

            for (Reloadable reloadable : ReloadableRegistry.getRegisteredReloadables()) {
                if (!reloadable.reload()) {
                    sender.sendMessage("[GeyserHub] " + ChatColor.RED + "Failed to reload class: " + ChatColor.RESET + reloadable.getClass().toString());
                }
            }

            sender.sendMessage("[GeyserHub] Finished reload.");
        }
        return true;
    }
}
