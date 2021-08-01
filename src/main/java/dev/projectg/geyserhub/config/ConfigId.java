package dev.projectg.geyserhub.config;

/**
 * An enum containing the identities of all valid configuration files.
 */
public enum ConfigId {
    MAIN("config.yml", 5),
    SELECTOR("selector.yml", 1);

    public static final ConfigId[] VALUES = values();

    public final String fileName;
    public final int version;

    // todo: maybe load configs on enum init

    /**
     * @param fileName the filename, including the extension, of the configuration
     * @param version the version of the configuration
     */
    ConfigId(String fileName, int version) {
        this.fileName = fileName;
        this.version = version;
    }
}
