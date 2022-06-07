import dev.projectg.crossplatforms.spigot.common.GeyserHubConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class GeyserHubConvertTest {

    @TempDir
    private static File tempDirectory;

    private static ConfigurationNode ITEMS;
    private static ConfigurationNode FORMS;
    private static ConfigurationNode MENUS;

    @BeforeAll
    public static void setup() throws ConfigurateException {
        // todo: need to access the TestLogger in core/src/test and instantiate it here
        Path dir = tempDirectory.toPath();

        // load expected values
        ITEMS = loader(dir, "access-items.yml").load();
        FORMS = loader(dir, "bedrock-forms.yml").load();
        MENUS = loader(dir, "java-menus.yml").load();
    }

    @Test
    public void testConvert() throws IOException {
        Path converted = GeyserHubConverter.convert(tempDirectory.toPath().resolve("selector.yml"));
        ConfigurationNode convertedItems = loader(converted, "access-items.yml").load();
        ConfigurationNode convertedForms = loader(converted, "bedrock-forms.yml").load();
        ConfigurationNode convertedMenus = loader(converted, "java-menus.yml").load();

        Assertions.assertEquals(ITEMS, convertedItems);
        Assertions.assertEquals(FORMS, convertedForms);
        Assertions.assertEquals(MENUS, convertedMenus);
    }

    private static YamlConfigurationLoader loader(Path directory, String file) {
        return GeyserHubConverter.loader(directory, file);
    }
}
