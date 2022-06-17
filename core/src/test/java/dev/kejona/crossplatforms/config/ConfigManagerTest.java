package dev.projectg.crossplatforms.config;

import com.google.inject.Guice;
import com.google.inject.Inject;
import dev.projectg.crossplatforms.TestLogger;
import dev.projectg.crossplatforms.TestModule;
import dev.projectg.crossplatforms.action.SimpleAction;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.command.DispatchableCommandSerializer;
import dev.projectg.crossplatforms.handler.FormPlayer;
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
            builder.registerExact(DispatchableCommand.class, new DispatchableCommandSerializer());
            builder.registerExact(BedrockForm.class, new BedrockFormSerializer());
            builder.registerExact(FormImage.class, new FormImageSerializer());
            builder.registerExact(CustomComponent.class, new ComponentSerializer());
        });
        manager.getActionSerializer().simpleGenericAction("server", String.class, FakeServer.class);

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
