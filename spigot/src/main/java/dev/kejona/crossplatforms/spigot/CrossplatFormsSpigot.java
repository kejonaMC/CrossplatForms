package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.spigot.adapter.Version;
import dev.kejona.crossplatforms.spigot.adapter.SpigotAdapter;
import dev.kejona.crossplatforms.spigot.adapter.VersionMap;
import dev.kejona.crossplatforms.spigot.adapter.Versioned;
import dev.kejona.crossplatforms.spigot.v1_12_R1.Adapter_v1_12_R1;
import dev.kejona.crossplatforms.spigot.v1_13_R2.Adapter_v1_13_R2;
import dev.kejona.crossplatforms.spigot.v1_14_R1.Adapter_v1_14_R1;
import dev.kejona.crossplatforms.spigot.v1_8_R3.Adapter_v1_8_R3;
import dev.kejona.crossplatforms.spigot.v1_9_R2.Adapter_v1_9_R2;

import java.util.function.Supplier;

public class CrossplatFormsSpigot extends SpigotBase {

    private static final int SUPPORTED_MAJOR_VERSION = 1;
    private static final VersionMap<Supplier<SpigotAdapter>> INDEXER = new VersionMap<>(SUPPORTED_MAJOR_VERSION);

    static {
        INDEXER.put(new Version("1_8_R3"), Adapter_v1_8_R3::new);
        INDEXER.put(new Version("1_9_R2"), Adapter_v1_9_R2::new);
        INDEXER.put(new Version("1_12_R1"), Adapter_v1_12_R1::new);
        INDEXER.put(new Version("1_13_R2"), Adapter_v1_13_R2::new);
        INDEXER.put(new Version("1_14_R1"), Adapter_v1_14_R1::new);
    }

    @Override
    public Versioned<SpigotAdapter> findVersionAdapter() {
        // substring to remove the v
        Versioned<Supplier<SpigotAdapter>> adapterSupplier = INDEXER.lenientSearch(ClassNames.NMS_VERSION.substring(1));
        return Versioned.convertSupplierType(adapterSupplier);
    }
}
