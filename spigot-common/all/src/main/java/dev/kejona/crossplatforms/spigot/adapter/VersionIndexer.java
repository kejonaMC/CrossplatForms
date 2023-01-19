package dev.kejona.crossplatforms.spigot.adapter;

import dev.kejona.crossplatforms.spigot.v1_12_R1.Adapter_v1_12_R1;
import dev.kejona.crossplatforms.spigot.v1_13_R2.Adapter_v1_13_R2;
import dev.kejona.crossplatforms.spigot.v1_9_R2.Adapter_v1_9_R2;
import dev.kejona.crossplatforms.spigot.v1_14_R1.Adapter_v1_14_R1;
import dev.kejona.crossplatforms.spigot.v1_8_R3.Adapter_v1_8_R3;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;

public class VersionIndexer {

    private static final int SUPPORTED_MAJOR_VERSION = 1;
    private static final TreeMap<Version, Supplier<VersionAdapter>> ADAPTERS = new TreeMap<>();
    // todo: probably need to make the value generic so that this can be unit tested (some adapters rely on craft classes)

    static {
        ADAPTERS.put(new Version("1_8_R3"), Adapter_v1_8_R3::new);
        ADAPTERS.put(new Version("1_9_R2"), Adapter_v1_9_R2::new);
        ADAPTERS.put(new Version("1_12_R1"), Adapter_v1_12_R1::new);
        ADAPTERS.put(new Version("1_13_R2"), Adapter_v1_13_R2::new);
        ADAPTERS.put(new Version("1_14_R1"), Adapter_v1_14_R1::new);
    }

    @Nonnull
    private VersionAdapter adapter(Version version) {
        return Objects.requireNonNull(ADAPTERS.get(version)).get();
    }

    /**
     * @param nmsVersion x_y_Rz
     */
    public VersionIndexResult findLenientAdapter(String nmsVersion) throws IllegalArgumentException {
        Version version = new Version(nmsVersion);

        if (version.major() != SUPPORTED_MAJOR_VERSION) {
            return new VersionIndexResult(ADAPTERS.firstKey().nmsVersion());
        }

        if (ADAPTERS.containsKey(version)) {
            // Direct support for this version
            return new VersionIndexResult(adapter(version));
        }

        // Find adapter versions below and above the given version
        Version lower = ADAPTERS.floorKey(version);
        Version higher = ADAPTERS.ceilingKey(version);

        if (lower == null) {
            // Given version is lower than lowest adapter version
            return new VersionIndexResult(higher.nmsVersion());
        }
        if (higher == null) {
            // Given version is higher than highest adapter version
            return new VersionIndexResult(adapter(lower));
        }

        if (lower.minor() != version.minor()) {
            // Lower version is not the same "game version". eg 1.13 vs 1.14

            if (higher.minor() == version.minor()) {
                // The given version and higher adapter version only differ in patch version. The server should be updated.
                // eg 1.13  <  1.14.1  <  1.14.2
                //    lower    version    higher
                return new VersionIndexResult(adapter(higher), higher.nmsVersion());
            } else {
                // eg 1.13  <  1.14  <  1.15
                //    lower   version   higher
                return new VersionIndexResult(adapter(lower));
            }
        }

        // There is an adapter for the same minor version, but a lower patch version
        return new VersionIndexResult(adapter(lower));
    }
}
