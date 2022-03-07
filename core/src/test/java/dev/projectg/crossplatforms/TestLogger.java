package dev.projectg.crossplatforms;

public class TestLogger extends Logger {

    private final boolean printInfo;
    private boolean debug = false;
    private boolean failed = false;

    public TestLogger(boolean printInfo) {
        this.printInfo = printInfo;
    }

    @Override
    public void info(String message) {
        if (printInfo) {
            System.out.println("[INFO] " + message);
        }
    }

    @Override
    public void warn(String message) {
        System.out.println("[WARN] " + message);
        failed = true;
    }

    @Override
    public void severe(String message) {
        System.out.println("[SEVERE] " + message);
        failed = true;
    }

    @Override
    public void debug(String message) {
        if (debug && printInfo) {
            System.out.println("[DEBUG] " + message);
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
}
