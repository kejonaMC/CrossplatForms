package dev.projectg.geyserhub.config;

/**
 * An enum containing the identities of all valid configuration files.
 */
public enum ConfigId {
    MAIN("config.yml", 6),
    SELECTOR("selector.yml", 2);

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
