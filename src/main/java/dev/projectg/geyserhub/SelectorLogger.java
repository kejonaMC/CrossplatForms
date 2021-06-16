package dev.projectg.geyserhub;

import dev.projectg.geyserhub.reloadable.Reloadable;
import dev.projectg.geyserhub.reloadable.ReloadableRegistry;

public class SelectorLogger implements Reloadable {

    private static final SelectorLogger LOGGER = new SelectorLogger(GeyserHubMain.getInstance());
    private static String message;

    private final GeyserHubMain plugin;
    private static boolean debug;

    public static SelectorLogger getLogger() {
        return LOGGER;
    }

    private SelectorLogger(GeyserHubMain plugin) {
        this.plugin = plugin;
        debug = plugin.getConfig().getBoolean("Enable-Debug", false);
        ReloadableRegistry.registerReloadable(this);
    }

    public static void info(String message) {
        SelectorLogger.info(message);
    }
    public void warn(String message) {
        plugin.getLogger().warning(message);
    }
    public static void severe(String message) {
        SelectorLogger.severe(message);
    }
    public static void debug(String message) {
        if (debug) {
            SelectorLogger.info(message);
        }
    }

    public boolean isDebug() {
        return debug;
    }

    @Override
    public boolean reload() {
        debug = plugin.getConfig().getBoolean("Enable-Debug", false);
        return true;
    }
}
