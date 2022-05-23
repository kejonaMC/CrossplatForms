package dev.projectg.crossplatforms;

public abstract class Logger {

    private static Logger INSTANCE;

    public Logger() {
        INSTANCE = this;
        if (Boolean.getBoolean("CrossplatForms.Debug")) {
            setDebug(true);
        }
    }

    public static Logger getLogger() {
        return INSTANCE;
    }

    public void log(Level level, String message) {
        switch (level) {
            case WARN:
                warn(message);
                break;
            case SEVERE:
                severe(message);
                break;
            case DEBUG:
                debug(message);
                break;
            default:
                info(message);
                break;
        }
    }

    /**
     * Dumps the current stack if debug mode is enabled.
     */
    public void debugStack() {
        if (isDebug()) {
            Thread.dumpStack();
        }
    }

    public abstract void info(String message);
    public abstract void warn(String message);
    public abstract void severe(String message);
    public abstract void debug(String message);

    public abstract boolean isDebug();
    public abstract void setDebug(boolean debug);

    public enum Level {
        INFO,
        WARN,
        SEVERE,
        DEBUG
    }
}
