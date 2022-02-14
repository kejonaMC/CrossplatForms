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
    public static void register(@Nonnull Reloadable reloadable) {
        reloadables.add(reloadable);
    }

    public static boolean reloadAll() {
        Logger logger = Logger.getLogger();

        boolean success = true;

        ConfigManager configManager = CrossplatForms.getInstance().getConfigManager();
        if (!configManager.loadAllConfigs()) {
            logger.severe("A severe configuration error occurred, which will lead to significant parts of this plugin not loading. Please repair the config and run /forms reload or restart the server.");
            success = false;
        }
        logger.setDebug(configManager.getConfig(GeneralConfig.class).map(GeneralConfig::isEnableDebug).orElse(false));
        logger.info("Reloaded the configuration, reloading modules...");

        for (Reloadable reloadable : reloadables) {
            logger.debug("Reloading " + reloadable.getClass().getSimpleName());
            if (!reloadable.reload()) {
                logger.severe("Failed to reload instance of: " + reloadable.getClass().getSimpleName());
                success = false;
            }
        }

        if (success) {
            logger.info("Successfully reloaded");
        } else {
            logger.severe("There was one or more errors reloading!");
        }

        return success;
    }
}
