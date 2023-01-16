package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.spigot.adapter.VersionIndexer;
import dev.kejona.crossplatforms.spigot.adapter.VersionAdapter;
import dev.kejona.crossplatforms.spigot.adapter.VersionIndexResult;
import dev.kejona.crossplatforms.spigot.v1_12_R1.Adapter_v1_12_R1;
import dev.kejona.crossplatforms.spigot.v1_13_R2.Adapter_v1_13_R2;
import dev.kejona.crossplatforms.spigot.v1_9_R2.Adapter_v1_9_R2;
import dev.kejona.crossplatforms.spigot.v1_14_R1.Adapter_v1_14_R1;
import dev.kejona.crossplatforms.spigot.v1_8_R3.Adapter_v1_8_R3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class VersionIndexerTest {

    private static VersionIndexer adapters;
    private static final String[] UNSUPPORTED_VERSIONS = {"1_7_R4", "1_8_R1", "1_8_R2"};
    private static final String[] UNDESIRED_VERSIONS = {"1_9_R1", "1_13_R1"};
    private static final Map<String, Class<? extends VersionAdapter>> EXPECTATIONS = new HashMap<>();

    @BeforeAll
    public static void setup() {
        adapters = new VersionIndexer();

        Class<? extends VersionAdapter> latestAdapter = Adapter_v1_14_R1.class;

        EXPECTATIONS.put("1_8_R3", Adapter_v1_8_R3.class);
        EXPECTATIONS.put("1_9_R1", Adapter_v1_9_R2.class); // yikes
        EXPECTATIONS.put("1_9_R2", Adapter_v1_9_R2.class);
        EXPECTATIONS.put("1_10_R1", Adapter_v1_9_R2.class);
        EXPECTATIONS.put("1_11_R1", Adapter_v1_9_R2.class);
        EXPECTATIONS.put("1_12_R1", Adapter_v1_12_R1.class);
        EXPECTATIONS.put("1_13_R1", Adapter_v1_13_R2.class); // yikes
        EXPECTATIONS.put("1_13_R2", Adapter_v1_13_R2.class);
        EXPECTATIONS.put("1_14_R1", Adapter_v1_14_R1.class);
        EXPECTATIONS.put("1_15_R1", latestAdapter);
        EXPECTATIONS.put("1_16_R1", latestAdapter);
        EXPECTATIONS.put("1_16_R2", latestAdapter);
        EXPECTATIONS.put("1_16_R3", latestAdapter);
        EXPECTATIONS.put("1_17_R1", latestAdapter);
        EXPECTATIONS.put("1_18_R1", latestAdapter);
        EXPECTATIONS.put("1_18_R2", latestAdapter);
        EXPECTATIONS.put("1_19_R1", latestAdapter);
        EXPECTATIONS.put("1_19_R2", latestAdapter);
    }

    @Test
    public void testSupportedVersions() {
        for (Map.Entry<String, Class<? extends VersionAdapter>> entry : EXPECTATIONS.entrySet()) {
            String version = entry.getKey();
            Class<? extends VersionAdapter> clazz = entry.getValue();

            Assertions.assertInstanceOf(
                clazz,
                adapters.findLenientAdapter(version).adapter().orElse(null),
                "Unexpected adapter for version " + version
            );
        }
    }

    @Test
    public void testUndesiredVersions() {
        for (String version : UNDESIRED_VERSIONS) {
            Assertions.assertNotNull(adapters.findLenientAdapter(version).betterVersion().orElse(null));
        }
    }

    @Test
    public void testUnsupportedVersions() {
        for (String version : UNSUPPORTED_VERSIONS) {
            VersionIndexResult index = adapters.findLenientAdapter(version);

            Assertions.assertNull(index.adapter().orElse(null));
            Assertions.assertNotNull(index.betterVersion().orElse(null));
        }
    }
}
