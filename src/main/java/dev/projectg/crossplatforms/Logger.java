package dev.projectg.crossplatforms;

import lombok.Getter;
import lombok.Setter;

public class Logger {

    private static final Logger LOGGER = new Logger(CrossplatForms.getInstance());

    private final java.util.logging.Logger handle;

    @Getter
    @Setter
    private boolean debug;

    public static Logger getLogger() {
        return LOGGER;
    }

    private Logger(CrossplatForms plugin) {
        this.handle = plugin.getLogger();
    }

    public void log(Level level, String message) {
        switch (level) { // intentional fallthrough
            case INFO -> info(message);
            case WARN -> warn(message);
            case SEVERE -> severe(message);
            case DEBUG -> debug(message);
        }
    }
    public void info(String message) {
        handle.info(message);
    }
    public void warn(String message) {
        handle.warning(message);
    }
    public void severe(String message) {
        handle.severe(message);
    }
    public void debug(String message) {
        if (debug) {
            handle.info(message);
        }
    }
    public enum Level {
        INFO,
        WARN,
        SEVERE,
        DEBUG
    }
}
