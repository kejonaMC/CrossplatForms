package dev.projectg.geyserhub.module;

import dev.projectg.geyserhub.GeyserHubMain;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class Placeholders implements Listener {
    public static Economy economy = null;
    public static Permission permission = null;
    public static int PAPI = 1;
    public static int vault = 1;
    public static int essentials = 1;
    public static String[] colorCodes = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f"};
    public static ArrayList<Player> hide = new ArrayList<>();
    public static int isb;
    public static String g;
    public static String b;

    static {
        isb = GeyserHubMain.getInstance().getConfig().getInt("Scoreboard.Refresh-rate");
        g = "N/A";
        b = "N/A";
    }

    public Placeholders() {
    }

    public static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    // todo I dont think we need this...
    public static String replaceValues(Player player, String x) {
        if (vault != 0 && permission.hasGroupSupport()) {
            g = permission.getPrimaryGroup(player);
        }

        if (essentials != 0) {
            b = String.valueOf(economy.getBalance(player));
        }

        String m = ChatColor.translateAlternateColorCodes('&', x.replace("{playerName}", player.getName()).replace("{onlinePlayers}", String.valueOf(Bukkit.getOnlinePlayers().size())).replace("{maxPlayers}", String.valueOf(Bukkit.getMaxPlayers())).replace("{Group}", g).replace("{Money}", b)).replace("null", "N/A");
        return PAPI == 1 ? PlaceholderAPI.setPlaceholders(player, m) : m;
    }
}
