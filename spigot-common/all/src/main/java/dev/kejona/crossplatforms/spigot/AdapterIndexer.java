package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.spigot.adapter.VersionAdapter;
import dev.kejona.crossplatforms.spigot.v1_12_R1.Adapter_v1_12_R1;
import dev.kejona.crossplatforms.spigot.v1_13_R2.Adapter_v1_13_R2;
import dev.kejona.crossplatforms.spigot.v1_9_R2.Adapter_v1_9_R2;
import dev.kejona.crossplatforms.spigot.v1_14_R1.Adapter_v1_14_R1;
import dev.kejona.crossplatforms.spigot.v1_8_R3.Adapter_v1_8_R3;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;

public class AdapterIndexer {

    private static final int SUPPORTED_MAJOR_VERSION = 1;
    private static final TreeMap<Version, Supplier<VersionAdapter>> ADAPTERS = new TreeMap<>();

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
    public IndexResult findLenientAdapter(String nmsVersion) throws IllegalArgumentException {
        Version version = new Version(nmsVersion);

        if (version.major() != SUPPORTED_MAJOR_VERSION) {
            return new IndexResult(ADAPTERS.firstKey().nmsVersion());
        }

        if (ADAPTERS.containsKey(version)) {
            // Direct support for this version
            return new IndexResult(adapter(version));
        }

        // Find adapter versions below and above the given version
        Version lower = ADAPTERS.floorKey(version);
        Version higher = ADAPTERS.ceilingKey(version);

        if (lower == null) {
            // Given version is lower than lowest adapter version
            return new IndexResult(higher.nmsVersion());
        }
        if (higher == null) {
            // Given version is higher than highest adapter version
            return new IndexResult(adapter(lower));
        }

        if (lower.minor() != version.minor()) {
            // Lower version is not the same "game version". eg 1.13 vs 1.14

            if (higher.minor() == version.minor()) {
                // The given version and higher adapter version only differ in patch version. The server should be updated.
                // eg 1.13  <  1.14.1  <  1.14.2
                //    lower    version    higher
                return new IndexResult(adapter(higher), higher.nmsVersion());
            } else {
                // eg 1.13  <  1.14  <  1.15
                //    lower   version   higher
                return new IndexResult(adapter(lower));
            }
        }

        // There is an adapter for the same minor version, but a lower patch version
        return new IndexResult(adapter(lower));
    }
}
