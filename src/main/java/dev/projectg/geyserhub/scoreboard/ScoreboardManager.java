package dev.projectg.geyserhub.scoreboard;

import dev.projectg.geyserhub.GeyserHubMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.Objects;


public class ScoreboardManager extends Placeholders {
    public ScoreboardManager() {
    }

    public static void addScoreboard() {

        for (Player all : Bukkit.getOnlinePlayers()) {
            createScoreboard(all);
        }

    }

    public static void createScoreboard(Player player) {
        Scoreboard board = Objects.requireNonNull(Bukkit.getServer().getScoreboardManager()).getNewScoreboard();
        Objective o = board.registerNewObjective("Scoreboard", "dummy");
        o.setDisplayName(replaceValues(player, GeyserHubMain.getInstance().getConfig().getString("Scoreboard.Title")));
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        List<String> text = GeyserHubMain.getInstance().getConfig().getStringList("Scoreboard.Line");
        int size = text.size();
        String f = "";

        for (String s : text) {
            f = replaceValues(player, s);
            int currentLine = size - 1;
            if (currentLine <= 15 && currentLine-- > 0) {
                f = f + colorcodes[currentLine--];
            }

            Score var10 = o.getScore(ChatColor.translateAlternateColorCodes('&', f));
            --size;
            var10.setScore(size);
        }
        player.setScoreboard(board);
    }
}
