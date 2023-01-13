package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.utils.ReflectionUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Thanks to Floodgate
 * https://github.com/GeyserMC/Floodgate/blob/master/spigot/src/main/java/org/geysermc/floodgate/util/ClassNames.java
 */
public final class ClassNames {

    public static final Method GET_PROFILE_METHOD;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        Class<?> craftPlayerClass = ReflectionUtils.getClass("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");

        GET_PROFILE_METHOD = ReflectionUtils.getMethod(craftPlayerClass, "getProfile");
        Objects.requireNonNull(GET_PROFILE_METHOD, "Get profile method");
    }
}
