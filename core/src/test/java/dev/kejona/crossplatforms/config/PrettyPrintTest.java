package dev.kejona.crossplatforms.config;

import dev.kejona.crossplatforms.utils.ConfigurateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class PrettyPrintTest {

    private static final String ALL = "null:\n" +
            "    actions:\n" +
            "        message: [WARN] Hello\n" +
            "        messages:\n" +
            "            prefix: [INFO]\n" +
            "            list:\n" +
            "                0: One\n" +
            "                1: Two\n" +
            "                2: Three";

    private static final String ALL_NO_KEY = "actions:\n" +
            "    message: [WARN] Hello\n" +
            "    messages:\n" +
            "        prefix: [INFO]\n" +
            "        list:\n" +
            "            0: One\n" +
            "            1: Two\n" +
            "            2: Three";

    private static final String ACTIONS = "actions:\n" +
            "    message: [WARN] Hello\n" +
            "    messages:\n" +
            "        prefix: [INFO]\n" +
            "        list:\n" +
            "            0: One\n" +
            "            1: Two\n" +
            "            2: Three";

    private static final String ACTIONS_NO_KEY = "message: [WARN] Hello\n" +
            "messages:\n" +
            "    prefix: [INFO]\n" +
            "    list:\n" +
            "        0: One\n" +
            "        1: Two\n" +
            "        2: Three";

    private static final String LIST_NOTATION = "actions:\n" +
            "    message: [WARN] Hello\n" +
            "    messages:\n" +
            "        prefix: [INFO]\n" +
            "        list:\n" +
            "            - One\n" +
            "            - Two\n" +
            "            - Three";

    @TempDir
    private static File directory;

    private final ConfigurationNode node;

    private PrettyPrintTest() throws IOException {
        YamlConfigurationLoader loader = ConfigurateUtils.loaderBuilder(directory, "KeyedTypeConfig.yml").build();
        node = loader.load();
    }

    @Test
    public void testShowKey() {
        PrettyPrinter printer = new PrettyPrinter(4, true);

        Assertions.assertEquals(ALL, printer.pretty(node, true));
        Assertions.assertEquals(ALL_NO_KEY, printer.pretty(node));
        Assertions.assertEquals(ALL_NO_KEY, printer.pretty(node, false));

        ConfigurationNode actions = node.node("actions");
        Assertions.assertEquals(ACTIONS, printer.pretty(actions, true));
        Assertions.assertEquals(ACTIONS, printer.pretty(actions));
        Assertions.assertEquals(ACTIONS_NO_KEY, printer.pretty(actions, false));
    }

    @Test
    public void testNoIndexList() {
        PrettyPrinter printer = new PrettyPrinter(4, false);

        Assertions.assertEquals(LIST_NOTATION, printer.pretty(node));
    }
}
