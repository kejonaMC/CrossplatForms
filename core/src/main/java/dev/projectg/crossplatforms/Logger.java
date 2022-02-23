package dev.projectg.crossplatforms;

public abstract class Logger {

    private static Logger INSTANCE;

    public Logger() {
        INSTANCE = this;
    }

    public static Logger getLogger() {
        return INSTANCE;
    }

    public void log(Level level, String message) {
        switch (level) {
            case INFO -> info(message);
            case WARN -> warn(message);
            case SEVERE -> severe(message);
            case DEBUG -> debug(message);
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
