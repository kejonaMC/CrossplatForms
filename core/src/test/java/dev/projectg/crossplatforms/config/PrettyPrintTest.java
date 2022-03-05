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

    @Test
    public void print(@TempDir File directory) throws IOException {
        YamlConfigurationLoader loader = ConfigurateUtils.loaderBuilder(directory, "KeyedTypeConfig.yml").build();
        ConfigurationNode base = loader.load();
        PrettyPrinter printer = new PrettyPrinter(2);

        Assertions.assertEquals(ALL, printer.pretty(base, true));
        Assertions.assertEquals(ALL_NO_KEY, printer.pretty(base));
        Assertions.assertEquals(ALL_NO_KEY, printer.pretty(base, false));

        ConfigurationNode actions = base.node("actions");
        Assertions.assertEquals(ACTIONS, printer.pretty(actions, true));
        Assertions.assertEquals(ACTIONS, printer.pretty(actions));
        Assertions.assertEquals(ACTIONS_NO_KEY, printer.pretty(actions, false));
    }
}
