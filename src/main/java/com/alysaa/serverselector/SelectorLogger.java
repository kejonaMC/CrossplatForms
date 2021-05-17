package com.alysaa.serverselector;

public class SelectorLogger {

    private static SelectorLogger LOGGER;

    private final GServerSelector instance;

    private SelectorLogger(GServerSelector instance) {
        this.instance = instance;
    }
    public static SelectorLogger getLogger() {
        if (LOGGER == null) {
            LOGGER = new SelectorLogger(GServerSelector.getInstance());
        }
        return LOGGER;
    }

    public void info(String message) {
        instance.getLogger().info(message);
    }
    public void warn(String message) {
        instance.getLogger().warning(message);
    }
    public void severe(String message) {
        instance.getLogger().severe(message);
    }
    public void debug(String message) {
        if (instance.getConfig().getBoolean("EnableDebug", false)) {
            instance.getLogger().info(message);
        }
    }
}
