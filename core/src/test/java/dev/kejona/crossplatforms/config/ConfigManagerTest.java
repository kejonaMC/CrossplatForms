package dev.kejona.crossplatforms.config;

import com.google.inject.Guice;
import com.google.inject.Inject;
import dev.kejona.crossplatforms.TestLogger;
import dev.kejona.crossplatforms.TestModule;
import dev.kejona.crossplatforms.action.BedrockTransferAction;
import dev.kejona.crossplatforms.action.SimpleAction;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockFormSerializer;
import dev.kejona.crossplatforms.interfacing.bedrock.FormConfig;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.ComponentSerializer;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Map;

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
        manager.getActionSerializer().simpleGenericAction("server", String.class, FakeServer.class);
        manager.getActionSerializer().genericAction(BedrockTransferAction.TYPE, BedrockTransferAction.class);

        manager.register(config);
        Assertions.assertTrue(manager.load());
        //Assertions.assertFalse(logger.failed());
        Assertions.assertEquals("", logger.warningDump());
    }

    public static class FakeServer extends SimpleAction<String> {

        @Inject
        private FakeServer() {
            super("server", "");
        }

        @Override
        public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders) {
            //no-op
        }
    }
}
