package dev.projectg.crossplatforms;

import org.bukkit.Bukkit;

public class JavaUtilLogger extends Logger {

    private final java.util.logging.Logger handle;
    private boolean debug = false;

    public JavaUtilLogger() {
        handle = Bukkit.getLogger();

    }

    @Override
    public void info(String message) {
        handle.info(message);
    }

    @Override
    public void warn(String message) {
        handle.warning(message);
    }

    @Override
    public void severe(String message) {
        handle.severe(message);
    }

    @Override
    public void debug(String message) {
        if (debug) {
            handle.info(message);
        }
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
