package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.utils.ReflectionUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Thanks to Floodgate
 * https://github.com/GeyserMC/Floodgate/blob/master/spigot/src/main/java/org/geysermc/floodgate/util/ClassNames.java
 */
public final class ClassNames {

    /**
     * Includes the v at the front
     */
    public static final String NMS_VERSION;
    private static final String CRAFTBUKKIT_PACKAGE;

    public static final Method PLAYER_GET_PROFILE;
    public static final Field META_SKULL_PROFILE;

    static {
        NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit." + NMS_VERSION;

        Class<?> craftPlayer = ReflectionUtils.requireClass(CRAFTBUKKIT_PACKAGE + ".entity.CraftPlayer");
        PLAYER_GET_PROFILE = ReflectionUtils.requireMethod(craftPlayer, "getProfile");

        Class<?> craftMetaSkull = ReflectionUtils.requireClass(CRAFTBUKKIT_PACKAGE + ".inventory.CraftMetaSkull");
        META_SKULL_PROFILE = ReflectionUtils.requireField(craftMetaSkull, "profile");
    }
}
