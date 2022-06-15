import dev.projectg.crossplatforms.TestLogger;
import dev.projectg.crossplatforms.config.PrettyPrinter;
import dev.projectg.crossplatforms.spigot.common.GeyserHubConverter;
import dev.projectg.crossplatforms.utils.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GeyserHubConvertTest {

    @TempDir
    private static File tempDirectory;

    private static final PrettyPrinter PRINTER = new PrettyPrinter(2, false);

    private static ConfigurationNode ITEMS;
    private static ConfigurationNode FORMS;
    private static ConfigurationNode MENUS;

    @BeforeAll
    public static void setup() throws IOException {
        new TestLogger(); // setup for Logger#get()

        // load expected values
        ITEMS = resourceLoader(tempDirectory, "access-items.yml").load();
        FORMS = resourceLoader(tempDirectory, "bedrock-forms.yml").load();
        MENUS = resourceLoader(tempDirectory, "java-menus.yml").load();
    }

    @Test
    public void testConvert() throws IOException {
        File selector = FileUtils.fileOrCopiedFromResource(new File(tempDirectory, "selector.yml"));
        File converted = GeyserHubConverter.convert(selector);
        Objects.requireNonNull(converted);
        ConfigurationNode convertedItems = loader(converted, "access-items.yml").load();
        ConfigurationNode convertedForms = loader(converted, "bedrock-forms.yml").load();
        ConfigurationNode convertedMenus = loader(converted, "java-menus.yml").load();

        Assertions.assertEquals(PRINTER.pretty(ITEMS), PRINTER.pretty(convertedItems));
        Assertions.assertEquals(PRINTER.pretty(FORMS), PRINTER.pretty(convertedForms));
        Assertions.assertEquals(PRINTER.pretty(MENUS), PRINTER.pretty(convertedMenus));
    }

    private static YamlConfigurationLoader loader(File directory, String file) {
        return GeyserHubConverter.loader(directory, file);
    }

    private static YamlConfigurationLoader resourceLoader(File targetDirectory, String resourceFileName) throws IOException {
        return GeyserHubConverter.loader(FileUtils.fileOrCopiedFromResource(new File(targetDirectory, resourceFileName)));
    }
}
