package dev.projectg.crossplatforms.reloadable;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.config.GeneralConfig;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class ReloadableRegistry {

    /**
     * A set of instances that implement the Reloadable interface
     */
    private static final Set<Reloadable> reloadables = new HashSet<>();

    public static void clear() {
        reloadables.clear();
    }

    /**
     * Register a reloadable
     * @param reloadable the reloadable
     */
    public static void registerReloadable(@Nonnull Reloadable reloadable) {
        reloadables.add(reloadable);
    }

    public static boolean reloadAll() {
        Logger logger = Logger.getLogger();

        ConfigManager configManager = CrossplatForms.getInstance().getConfigManager();
        if (!configManager.loadAllConfigs()) {
            return false;
        }
        logger.setDebug(configManager.getConfig(GeneralConfig.class).isEnableDebug());
        logger.info("Reloaded the configuration, reloading modules...");

        boolean success = true;
        for (Reloadable reloadable : reloadables) {
            if (!reloadable.reload()) {
                logger.severe("Failed to reload class: " + reloadable.getClass().toString());
                success = false;
            }
        }

        if (success) {
            logger.info("Successfully reloaded");
        } else {
            logger.severe("There was an error reloading!");
        }

        return success;
    }
}
