package dev.projectg.crossplatforms.config.form;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.TestLogger;
import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.config.ConfigManagerTest;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.FormConfig;
import dev.projectg.crossplatforms.interfacing.bedrock.FormImageSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.ComponentSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import org.geysermc.cumulus.util.FormImage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class FormConfigUpdaterTest {

    private static final int OLD_VERSION = 1;
    private static final int CURRENT_VERSION = FormConfig.VERSION;

    @TempDir
    private static Path directory;

    @Test
    public void testLoadAndUpdateFormConfig() {
        TestLogger logger = new TestLogger(false);
        // configs to load
        ConfigId oldest = id("bedrock-forms-old.yml");
        ConfigId current = id("bedrock-forms.yml");
        ConfigManager manager = new ConfigManager(directory, logger);
        // serializers
        CrossplatForms.registerDefaultActions(manager);
        manager.getActionSerializer().registerSimpleType("server", String.class, ConfigManagerTest.FakeServer::new);
        manager.serializers(builder -> {
            builder.registerExact(BedrockForm.class, new BedrockFormSerializer());
            builder.registerExact(FormImage.class, new FormImageSerializer());
            builder.registerExact(CustomComponent.class, new ComponentSerializer());
        });
        // register configs
        manager.register(oldest);
        manager.register(current);

        Assertions.assertTrue(manager.load());
        Assertions.assertEquals(manager.getNode(oldest.clazz), manager.getNode(current.clazz));
        Assertions.assertFalse(logger.failed());
    }

    private static ConfigId id(String name) {
        return new ConfigId("configs/forms/" + name, CURRENT_VERSION, OLD_VERSION, FormConfig.class, FormConfig::updater);
    }
}
