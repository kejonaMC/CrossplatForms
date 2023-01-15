package dev.kejona.crossplatforms.spigot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@Getter
@EqualsAndHashCode
public class Version implements Comparable<Version> {
    private final int major;
    private final int minor;
    private final int patch;

    public Version(String nmsVersion) throws IllegalArgumentException {
        try {
            String[] split = nmsVersion.split("_", 3);

            major = Integer.parseUnsignedInt(split[0]);
            minor = Integer.parseUnsignedInt(split[1]);
            patch = Integer.parseUnsignedInt(split[2].substring(1)); // substring to remove the R
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse NMS version: " + nmsVersion, e);
        }
    }

    public String nmsVersion() {
        return major + "_" + minor + "_R" + patch;
    }

    @Override
    public int compareTo(@NotNull Version other) {
        if (major < other.major) {
            return -1;
        } else if (major > other.major) {
            return 1;
        }
        // major version the same

        if (minor < other.minor) {
            return -1;
        } else if (minor > other.minor) {
            return 1;
        }
        // minor the same

        if (patch < other.patch) {
            return -1;
        } else if (patch > other.patch) {
            return 1;
        }

        return 0; // the same
    }
}
