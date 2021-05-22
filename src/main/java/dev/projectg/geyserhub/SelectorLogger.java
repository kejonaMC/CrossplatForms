package dev.projectg.geyserhub;

public class SelectorLogger {

    private static final SelectorLogger LOGGER = new SelectorLogger(GeyserHubMain.getInstance());

    private final GeyserHubMain instance;

    private SelectorLogger(GeyserHubMain instance) {
        this.instance = instance;
    }
    public static SelectorLogger getLogger() {
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
