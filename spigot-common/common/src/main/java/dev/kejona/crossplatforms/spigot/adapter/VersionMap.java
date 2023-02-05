package dev.kejona.crossplatforms.spigot.adapter;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.TreeMap;

public class VersionMap<T> extends TreeMap<Version, T> {

    private final int supportedMajorVersion;

    public VersionMap(int supportedMajorVersion) {
        this.supportedMajorVersion = supportedMajorVersion;
    }

    @Nonnull
    private T adapter(Version version) {
        return Objects.requireNonNull(get(version));
    }

    /**
     * @param nmsVersion x_y_Rz
     */
    public Versioned<T> findLenientAdapter(String nmsVersion) throws IllegalArgumentException {
        if (isEmpty()) {
           throw new IllegalStateException("The map is empty");
        }

        Version version = new Version(nmsVersion);

        if (version.major() != supportedMajorVersion) {
            return new Versioned<>(firstKey().nmsVersion());
        }

        if (containsKey(version)) {
            // Direct support for this version
            return new Versioned<>(adapter(version));
        }

        // Find adapter versions below and above the given version
        Version lower = floorKey(version);
        Version higher = ceilingKey(version);

        if (lower == null) {
            // Given version is lower than lowest adapter version
            return new Versioned<>(higher.nmsVersion());
        }
        if (higher == null) {
            // Given version is higher than highest adapter version
            return new Versioned<>(adapter(lower));
        }

        if (lower.minor() != version.minor()) {
            // Lower version is not the same "game version". eg 1.13 vs 1.14

            if (higher.minor() == version.minor()) {
                // The given version and higher adapter version only differ in patch version. The server should be updated.
                // eg 1.13  <  1.14.1  <  1.14.2
                //    lower    version    higher
                return new Versioned<>(adapter(higher), higher.nmsVersion());
            } else {
                // eg 1.13  <  1.14  <  1.15
                //    lower   version   higher
                return new Versioned<>(adapter(lower));
            }
        }

        // There is an adapter for the same minor version, but a lower patch version
        return new Versioned<>(adapter(lower));
    }
}
