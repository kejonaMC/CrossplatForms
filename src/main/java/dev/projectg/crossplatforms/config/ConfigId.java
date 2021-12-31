package dev.projectg.crossplatforms.config;

/**
 * An enum containing the identities of all valid configuration files.
 */
public enum ConfigId {
    MAIN("config.yml", 1),
    SELECTOR("selector.yml", 1);

    public static final ConfigId[] VALUES = values();

    public final String fileName;
    public final int version;

    /**
     * @param fileName the filename, including the extension, of the configuration
     * @param version the version of the configuration
     */
    ConfigId(String fileName, int version) {
        this.fileName = fileName;
        this.version = version;
    }
}
