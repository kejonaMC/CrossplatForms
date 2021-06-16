package dev.projectg.geyserhub.reloadable;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.ConfigManager;
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
        SelectorLogger logger = SelectorLogger.getLogger();

        ConfigManager configManager = GeyserHubMain.getInstance().getConfigManager();
        // loadConfiguration() will never remove a key so I don't think this will result in ConcurrentModificationException...
        for (String configName : configManager.getAllFileConfigurations().keySet()) {
            if (configManager.loadConfiguration(configName)) {
                logger.debug("Reloaded config file: " + configName + ".yml");
            } else {
                logger.severe(ChatColor.RED + "Failed to reload configuration: " + configName + ".yml");
                return false;
            }
        }
        logger.info("Reloaded the configuration, reloading modules...");

        boolean success = true;
        for (Reloadable reloadable : ReloadableRegistry.getRegisteredReloadables()) {
            if (!reloadable.reload()) {
                logger.severe(ChatColor.RED + "Failed to reload class: " + ChatColor.RESET + reloadable.getClass().toString());
                success = false;
            }
        }

        logger.info("Finished reload.");
        return success;
    }
}
