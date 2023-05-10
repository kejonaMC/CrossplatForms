package dev.kejona.crossplatforms.spigot.adapter;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.TreeMap;

public class VersionMap<T> extends TreeMap<Version, T> {

    private final int majorVersion;

    public VersionMap(int supportedMajorVersion) {
        this.majorVersion = supportedMajorVersion;
    }

    @Nonnull
    private T getOrThrow(Version version) {
        return Objects.requireNonNull(get(version));
    }

    /**
     * @param nmsVersion x_y_Rz
     */
    public Versioned<T> lenientSearch(String nmsVersion) throws IllegalArgumentException {
        if (isEmpty()) {
           throw new IllegalStateException("The map is empty");
        }

        Version version = new Version(nmsVersion);

        if (version.major() != majorVersion) {
            return Versioned.unsupported(lastKey().toString());
        }

        if (containsKey(version)) {
            // Direct support for this version
            return Versioned.supported(getOrThrow(version));
        }

        // Find adapter versions below and above the given version
        Version lower = floorKey(version);
        Version higher = ceilingKey(version);

        if (lower == null) {
            // Given version is lower than the lowest adapter version
            return Versioned.unsupported(higher.toString());
        }
        if (higher == null) {
            // Given version is higher than the highest adapter version. Assume we know this works
            return Versioned.supported(getOrThrow(lower));
        }

        if (lower.minor() != version.minor()) {
            // Lower version is not the same "game version". eg 1.13 vs 1.14

            if (higher.minor() == version.minor()) {
                // The given version and higher adapter version only differ in patch version. The server should be updated.
                // eg 1.13  <  1.14.1  <  1.14.2
                //    lower    version    higher
                return Versioned.supported(getOrThrow(higher), higher.toString());
            } else {
                // eg 1.13  <  1.14  <  1.15
                //    lower   version   higher
                return Versioned.supported(getOrThrow(lower));
            }
        }

        // There is an adapter for the same minor version, but a lower patch version
        return Versioned.supported(getOrThrow(lower));
    }
}
