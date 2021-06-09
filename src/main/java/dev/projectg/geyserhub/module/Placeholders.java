package dev.projectg.geyserhub.module;

import dev.projectg.geyserhub.GeyserHubMain;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Placeholders {
    public static String[] colorCodes = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f"};
    public static ArrayList<Player> hide = new ArrayList<>();
    public static int refreshRate = GeyserHubMain.getInstance().getConfig().getInt("Scoreboard.Refresh-rate");
}
