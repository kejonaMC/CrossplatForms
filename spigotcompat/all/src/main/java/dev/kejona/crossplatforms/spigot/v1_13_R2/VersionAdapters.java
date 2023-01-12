package dev.kejona.crossplatforms.spigot.v1_13_R2;

import dev.kejona.crossplatforms.spigot.common.adapter.VersionAdapter;
import dev.kejona.crossplatforms.spigot.v1_12_R1.Adapter_v1_12_R1;
import dev.kejona.crossplatforms.spigot.v1_9_R2.Adapter_v1_9_R2;
import dev.kejonamc.crossplatforms.spigot.v1_14_R1.Adapter_v1_14_R1;
import dev.kejonamc.crossplatforms.spigot.v1_8_R3.Adapter_v1_8_R3;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class VersionAdapters {

    private static final Map<String, Supplier<VersionAdapter>> ADAPTERS = new HashMap<>();

    static {
        ADAPTERS.put("1_8_R3", Adapter_v1_8_R3::new);

        ADAPTERS.put("1_9_R2", Adapter_v1_9_R2::new);
        ADAPTERS.put("1_10_R1", Adapter_v1_9_R2::new);
        ADAPTERS.put("1_11_R1", Adapter_v1_9_R2::new);

        ADAPTERS.put("1_12_R1", Adapter_v1_12_R1::new);
        ADAPTERS.put("1_13_R2", Adapter_v1_13_R2::new);
        ADAPTERS.put("1_14_R1", Adapter_v1_14_R1::new);
    }

    public static VersionAdapter adapterForVersion(String version) {
        if (!ADAPTERS.containsKey(version)) {
            return new Adapter_v1_14_R1(); // todo: improve this
        }

        return ADAPTERS.get(version).get();
    }
}
