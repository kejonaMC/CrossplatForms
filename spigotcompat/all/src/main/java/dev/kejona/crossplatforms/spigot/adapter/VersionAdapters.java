package dev.kejona.crossplatforms.spigot.adapter;

import dev.kejonamc.crossplatforms.spigot.v1_14_R1.Adapter_v1_14_R1;
import dev.kejonamc.crossplatforms.spigot.v1_8_R3.Adapter_v1_8_R3;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class VersionAdapters {

    private static final Map<String, Supplier<VersionAdapter>> ADAPTERS = new HashMap<>();

    static {
        ADAPTERS.put("1_8_R3", Adapter_v1_8_R3::new);
        ADAPTERS.put("1_14_R1", Adapter_v1_14_R1::new);
    }

    public static VersionAdapter adapterForVersion(String version) {
        if (!ADAPTERS.containsKey(version)) {
            return new Adapter_v1_14_R1();
        }

        return ADAPTERS.get(version).get();
    }
}
