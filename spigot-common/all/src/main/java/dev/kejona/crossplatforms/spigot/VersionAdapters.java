package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.spigot.adapter.VersionAdapter;
import dev.kejona.crossplatforms.spigot.v1_12_R1.Adapter_v1_12_R1;
import dev.kejona.crossplatforms.spigot.v1_13_R2.Adapter_v1_13_R2;
import dev.kejona.crossplatforms.spigot.v1_9_R2.Adapter_v1_9_R2;
import dev.kejonamc.crossplatforms.spigot.v1_14_R1.Adapter_v1_14_R1;
import dev.kejonamc.crossplatforms.spigot.v1_8_R3.Adapter_v1_8_R3;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class VersionAdapters {

    private static final int SUPPORTED_MAJOR_VERSION = 1;
    private static final TreeMap<Version, Supplier<VersionAdapter>> ADAPTERS = new TreeMap<>();

    static {
        ADAPTERS.put(new Version("1_8_R3"), Adapter_v1_8_R3::new);
        ADAPTERS.put(new Version("1_9_R2"), Adapter_v1_9_R2::new);
        ADAPTERS.put(new Version("1_12_R1"), Adapter_v1_12_R1::new);
        ADAPTERS.put(new Version("1_13_R2"), Adapter_v1_13_R2::new);
        ADAPTERS.put(new Version("1_14_R1"), Adapter_v1_14_R1::new);
    }

    private final Logger logger;

    private VersionAdapter adapter(Version version) {
        return Objects.requireNonNull(ADAPTERS.get(version)).get();
    }

    /**
     * @param nmsVersion x_y_Rz
     */
    public VersionAdapter adapterForVersion(String nmsVersion) throws IllegalArgumentException {
        Version version = new Version(nmsVersion);

        if (version.major() != SUPPORTED_MAJOR_VERSION) {
            throw new IllegalArgumentException("Unsupported major version " + version.major() + " for: " + nmsVersion);
        }

        if (ADAPTERS.containsKey(version)) {
            // Direct support for this version
            return ADAPTERS.get(version).get();
        }

        // Find adapter versions below and above the given version
        Version lower = ADAPTERS.floorKey(version);
        Version higher = ADAPTERS.ceilingKey(version);

        if (lower == null) {
            // Given version is lower than lowest adapter version
            throw new IllegalArgumentException("Unsupported server version. " + higher.nmsVersion() + " or higher required.");
        }
        if (higher == null) {
            // Given version is higher than highest adapter version
            return adapter(lower);
        }

        if (lower.minor() != version.minor()) {
            // Lower version is not the same "game version". eg 1.13 vs 1.14

            if (higher.minor() == version.minor()) {
                // The given version and adapter version only differ in patch version
                // eg 1.13  <  1.14.1  <  1.14.2
                //    lower    version    higher
                logger.warning("You should update from " + version.nmsVersion() + " to " + higher.nmsVersion());
                return adapter(higher);
            } else {
                // eg 1.13  <  1.14  <  1.15
                //    lower   version   higher
                return adapter(lower);
            }
        }

        // There is an adapter for the same minor version, but a lower patch version
        return adapter(lower);
    }
}
