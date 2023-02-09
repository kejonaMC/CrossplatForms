package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.spigot.adapter.Version;
import dev.kejona.crossplatforms.spigot.adapter.VersionMap;
import dev.kejona.crossplatforms.spigot.adapter.Versioned;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class VersionMapTest {

    private static VersionMap<String> map;
    private static final String[] UNSUPPORTED_VERSIONS = {"1_7_R4", "1_8_R1", "1_8_R2"};
    private static final String[] UNDESIRED_VERSIONS = {"1_9_R1", "1_13_R1"};
    private static final Map<String, String> EXPECTATIONS = new HashMap<>();

    @BeforeAll
    public static void setup() {
        // Mirrored from the actual version indexer in the plugin
        map = new VersionMap<>(1);
        map.put(new Version("1_8_R3"), "1_8_R3");
        map.put(new Version("1_9_R2"), "1_9_R2");
        map.put(new Version("1_12_R1"), "1_12_R1");
        map.put(new Version("1_13_R2"), "1_13_R2");
        map.put(new Version("1_14_R1"), "1_14_R1");

        String latestAdapter = "1_14_R1";
        EXPECTATIONS.put("1_8_R3", "1_8_R3");
        EXPECTATIONS.put("1_9_R1", "1_9_R2"); // yikes
        EXPECTATIONS.put("1_9_R2", "1_9_R2");
        EXPECTATIONS.put("1_10_R1", "1_9_R2");
        EXPECTATIONS.put("1_11_R1", "1_9_R2");
        EXPECTATIONS.put("1_12_R1", "1_12_R1");
        EXPECTATIONS.put("1_13_R1", "1_13_R2"); // yikes
        EXPECTATIONS.put("1_13_R2", "1_13_R2");
        EXPECTATIONS.put("1_14_R1", "1_14_R1");
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
        for (Map.Entry<String, String> entry : EXPECTATIONS.entrySet()) {
            String version = entry.getKey();
            String expected = entry.getValue();

            Assertions.assertEquals(
                expected,
                map.lenientSearch(version).value().orElse(null),
                "Failed to find the right version for a supported version"
            );
        }
    }

    @Test
    public void testUndesiredVersions() {
        for (String version : UNDESIRED_VERSIONS) {
            Assertions.assertNotNull(
                map.lenientSearch(version).betterVersion().orElse(null),
                "Expected undesired version " + version + " to still result in a version"
            );
        }
    }

    @Test
    public void testUnsupportedVersions() {
        for (String version : UNSUPPORTED_VERSIONS) {
            Versioned<String> index = map.lenientSearch(version);

            // The value
            Assertions.assertNull(index.value().orElse(null),
                "Expected version " + version + " to provide a null version (unsupported)"
            );

            // The version recommendation
            Assertions.assertNotNull(
                index.betterVersion().orElse(null),
                "Expected version " + version + " to result in a recommendation for a better version"
            );
        }
    }
}
