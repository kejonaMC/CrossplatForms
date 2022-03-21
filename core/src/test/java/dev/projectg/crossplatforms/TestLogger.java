package dev.projectg.crossplatforms;

import java.util.ArrayList;
import java.util.List;

public class TestLogger extends Logger {

    private boolean debug = false;
    private boolean failed = false;
    private final List<String> messages = new ArrayList<>();

    @Override
    public void info(String message) {
        messages.add("[INFO] " + message);
    }

    @Override
    public void warn(String message) {
        messages.add("[WARN] " + message);
        failed = true;
    }

    @Override
    public void severe(String message) {
        messages.add("[SEVERE] " + message);
        failed = true;
    }

    @Override
    public void debug(String message) {
        if (debug) {
            messages.add("[DEBUG] " + message);
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

    public boolean failed() {
        return failed;
    }

    public String dump() {
        return String.join("\n", messages);
    }
}
