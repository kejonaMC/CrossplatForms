package dev.projectg.crossplatforms.spigot.common;

import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class SpigotCommon {
    public static final String PIE_CHART_LEGACY = "legacy";
    public static final int METRICS_ID = 14707;

    public static final LegacyComponentSerializer LEGACY_SERIALIZER = BukkitComponentSerializer.legacy();

}
