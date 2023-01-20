package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.spigot.adapter.Version;
import dev.kejona.crossplatforms.spigot.adapter.VersionAdapter;
import dev.kejona.crossplatforms.spigot.adapter.VersionMap;
import dev.kejona.crossplatforms.spigot.adapter.VersionValue;
import dev.kejona.crossplatforms.spigot.v1_12_R1.Adapter_v1_12_R1;
import dev.kejona.crossplatforms.spigot.v1_13_R2.Adapter_v1_13_R2;
import dev.kejona.crossplatforms.spigot.v1_14_R1.Adapter_v1_14_R1;
import dev.kejona.crossplatforms.spigot.v1_8_R3.Adapter_v1_8_R3;
import dev.kejona.crossplatforms.spigot.v1_9_R2.Adapter_v1_9_R2;

import java.util.function.Supplier;

public class CrossplatFormsSpigot extends SpigotBase {

    private static final int SUPPORTED_MAJOR_VERSION = 1;
    private static final VersionMap<Supplier<VersionAdapter>> INDEXER = new VersionMap<>(SUPPORTED_MAJOR_VERSION);

    static {
        INDEXER.put(new Version("1_8_R3"), Adapter_v1_8_R3::new);
        INDEXER.put(new Version("1_9_R2"), Adapter_v1_9_R2::new);
        INDEXER.put(new Version("1_12_R1"), Adapter_v1_12_R1::new);
        INDEXER.put(new Version("1_13_R2"), Adapter_v1_13_R2::new);
        INDEXER.put(new Version("1_14_R1"), Adapter_v1_14_R1::new);
    }

    @Override
    public VersionValue<VersionAdapter> findVersionAdapter() {
        // substring to remove the v
        return VersionValue.convertSupplierType(INDEXER.findLenientAdapter(ClassNames.NMS_VERSION.substring(1)));
    }
}
