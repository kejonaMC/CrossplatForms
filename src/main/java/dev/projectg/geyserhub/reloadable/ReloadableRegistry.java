package dev.projectg.geyserhub.reloadable;

import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.utils.ConfigManager;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class ReloadableRegistry {

    /**
     * A set of instances that implement the Reloadable interface
     */
    private static final Set<Reloadable> reloadables = new HashSet<>();

    /**
     * Register a reloadable
     * @param reloadable the reloadable
     */
    public static void registerReloadable(@Nonnull Reloadable reloadable) {
        reloadables.add(reloadable);
    }

    /**
     * @return A copy of all registered reloadables
     */
    public static Reloadable[] getRegisteredReloadables() {
        return reloadables.toArray(new Reloadable[0]);
    }

    public static boolean reloadAll() {

        if (ConfigManager.loadDefaultConfiguration()) {
            SelectorLogger.info("Reloaded the default configuration, reloading modules...");
        } else {
            SelectorLogger.severe(ChatColor.RED + "Failed to reload the configuration!");
            return false;
        }
        if (ConfigManager.loadSelectorConfiguration()) {
            SelectorLogger.info("Reloaded the Selector configuration, reloading modules...");
        } else {
            SelectorLogger.severe(ChatColor.RED + "Failed to reload the configuration!");
            return false;
        }

        boolean success = true;
        for (Reloadable reloadable : ReloadableRegistry.getRegisteredReloadables()) {
            if (!reloadable.reload()) {
                SelectorLogger.severe(ChatColor.RED + "Failed to reload class: " + ChatColor.RESET + reloadable.getClass().toString());
                success = false;
            }
        }

        SelectorLogger.info("Finished reload.");
        return success;
    }
}
