package dev.projectg.geyserhub.module;

import dev.projectg.geyserhub.GeyserHubMain;

import java.util.Objects;

public class Placeholders {

    public static final String[] colorCodes = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f"};
    public static int refreshRate = Objects.requireNonNull(GeyserHubMain.getInstance().getConfigManager().getFileConfiguration("config")).getInt("Scoreboard.Refresh-rate");
}
