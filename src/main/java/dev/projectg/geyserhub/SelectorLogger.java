package dev.projectg.geyserhub;

import dev.projectg.geyserhub.reloadable.Reloadable;
import dev.projectg.geyserhub.reloadable.ReloadableRegistry;

public class SelectorLogger implements Reloadable {

    private static final SelectorLogger LOGGER = new SelectorLogger(GeyserHubMain.getInstance());
    private static String message;

    private final GeyserHubMain plugin;
    private boolean debug;

    public static SelectorLogger getLogger() {
        return LOGGER;
    }

    private SelectorLogger(GeyserHubMain plugin) {
        this.plugin = plugin;
        debug = plugin.getConfig().getBoolean("Enable-Debug", false);
        ReloadableRegistry.registerReloadable(this);
    }

    public void info(String message) {
        plugin.getLogger().info(message);
    }
    public void warn(String message) {
        plugin.getLogger().warning(message);
    }
    public void severe(String message) {
        plugin.getLogger().severe(message);
    }
    public void debug(String message) {
        if (debug) {
            plugin.getLogger().info(message);
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
