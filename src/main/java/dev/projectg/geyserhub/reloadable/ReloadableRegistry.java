package dev.projectg.geyserhub.reloadable;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.config.ConfigId;
import dev.projectg.geyserhub.config.ConfigManager;
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

    public static boolean reloadAll() {
        SelectorLogger logger = SelectorLogger.getLogger();

        ConfigManager configManager = GeyserHubMain.getInstance().getConfigManager();
        // loadConfiguration() will never remove a key so I don't think this will result in ConcurrentModificationException...
        for (ConfigId configId : configManager.getAllFileConfigurations().keySet()) {
            if (configManager.loadConfig(configId)) {
                logger.debug("Reloaded config file: " + configId.fileName);
            } else {
                logger.severe(ChatColor.RED + "Failed to reload configuration: " + configId.fileName);
                return false;
            }
        }
        logger.info("Reloaded the configuration, reloading modules...");

        boolean success = true;
        for (Reloadable reloadable : reloadables) {
            if (!reloadable.reload()) {
                logger.severe(ChatColor.RED + "Failed to reload class: " + ChatColor.RESET + reloadable.getClass().toString());
                success = false;
            }
        }

        logger.info("Finished reload.");
        return success;
    }
}
