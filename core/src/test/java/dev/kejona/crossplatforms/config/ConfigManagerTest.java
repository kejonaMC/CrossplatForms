package dev.kejona.crossplatforms.config;

import com.google.inject.Guice;
import dev.kejona.crossplatforms.TestLogger;
import dev.kejona.crossplatforms.TestModule;
import dev.kejona.crossplatforms.action.ActionSerializer;
import dev.kejona.crossplatforms.action.BedrockTransferAction;
import dev.kejona.crossplatforms.action.ServerAction;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockFormSerializer;
import dev.kejona.crossplatforms.interfacing.bedrock.FormConfig;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.ComponentSerializer;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class ConfigManagerTest {

    @TempDir
    private Path directory;

    @Test
    public void testBasicConfig() {
        TestLogger logger = new TestLogger();
        ConfigId config = ConfigId.builder()
            .file("configs/forms/bedrock-forms-" + FormConfig.VERSION + ".yml")
            .version(FormConfig.VERSION)
            .clazz(FormConfig.class)
            .build();

        ConfigManager manager = new ConfigManager(directory, logger, Guice.createInjector(new TestModule()));
        manager.serializers(builder -> {
            builder.registerExact(BedrockForm.class, new BedrockFormSerializer());
            builder.registerExact(CustomComponent.class, new ComponentSerializer());
        });
        ActionSerializer actionSerializer = manager.getActionSerializer();
        ServerAction.register(actionSerializer);
        BedrockTransferAction.register(actionSerializer);

        manager.register(config);
        Assertions.assertTrue(manager.load());
        //Assertions.assertFalse(logger.failed());
        Assertions.assertEquals("", logger.warningDump());
    }
}
