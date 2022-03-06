package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.utils.ConfigurateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class PrettyPrintTest {

    private static final String ALL = """
            null:
              actions:
                message: [WARN] Hello
                messages:
                  prefix: [INFO]
                  list:
                    0: One
                    1: Two
                    2: Three""";

    private static final String ALL_NO_KEY = """
            actions:
              message: [WARN] Hello
              messages:
                prefix: [INFO]
                list:
                  0: One
                  1: Two
                  2: Three""";

    private static final String ACTIONS = """
            actions:
              message: [WARN] Hello
              messages:
                prefix: [INFO]
                list:
                  0: One
                  1: Two
                  2: Three""";

    private static final String ACTIONS_NO_KEY = """
            message: [WARN] Hello
            messages:
              prefix: [INFO]
              list:
                0: One
                1: Two
                2: Three""";

    private static final String LIST_NOTATION = """
            actions:
              message: [WARN] Hello
              messages:
                prefix: [INFO]
                list:
                  - One
                  - Two
                  - Three""";

    @TempDir
    private static File directory;

    private final ConfigurationNode node;

    private PrettyPrintTest() throws IOException {
        YamlConfigurationLoader loader = ConfigurateUtils.loaderBuilder(directory, "KeyedTypeConfig.yml").build();
        node = loader.load();
    }

    @Test
    public void testShowKey() {
        PrettyPrinter printer = new PrettyPrinter(2, false);

        Assertions.assertEquals(ALL, printer.pretty(node, true));
        Assertions.assertEquals(ALL_NO_KEY, printer.pretty(node));
        Assertions.assertEquals(ALL_NO_KEY, printer.pretty(node, false));

        ConfigurationNode actions = node.node("actions");
        Assertions.assertEquals(ACTIONS, printer.pretty(actions, true));
        Assertions.assertEquals(ACTIONS, printer.pretty(actions));
        Assertions.assertEquals(ACTIONS_NO_KEY, printer.pretty(actions, false));
    }

    @Test
    public void testIndexLists() {
        PrettyPrinter printer = new PrettyPrinter(2, true);

        Assertions.assertEquals(LIST_NOTATION, printer.pretty(node));
    }
}
