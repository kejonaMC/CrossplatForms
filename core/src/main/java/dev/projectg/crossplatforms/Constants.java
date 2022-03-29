package dev.projectg.crossplatforms;

import dev.projectg.crossplatforms.utils.FileUtils;

import java.io.IOException;
import java.util.Properties;

public final class Constants {
    public static final String NAME = "CrossplatForms";
    private static String ID = "crossplatforms";
    public static final String MESSAGE_PREFIX = "[CForms] ";

    private static String VERSION = "UNKNOWN";
    private static String BRANCH = "UNKNOWN";
    private static String COMMIT = "UNKNOWN";
    private static int BUILD_NUMBER = -1;

    public static String Id() {
        return ID;
    }
    public static void setId(String id) {
        ID = id;
    }

    public static String version() {
        return VERSION;
    }
    public static String branch() {
        return BRANCH;
    }
    public static String commit() {
        return COMMIT;
    }
    public static int buildNumber() {
        return BUILD_NUMBER;
    }

    public static void fetch() {
        Properties properties;
        try {
            properties = FileUtils.getProperties("build.properties");
        } catch (IOException e) {
            Logger.getLogger().warn("Failed to get build properties");
            e.printStackTrace();
            return;
        }

        VERSION = properties.getProperty("project_version", "UNKNOWN");
        BRANCH = properties.getProperty("git_branch", "UNKNOWN");
        COMMIT = properties.getProperty("git_commit", "UNKNOWN");
        try {
            BUILD_NUMBER = Integer.parseUnsignedInt(properties.getProperty("build_number", ""));
        } catch (NumberFormatException ignored) {
            // already has default
        }
    }
}
