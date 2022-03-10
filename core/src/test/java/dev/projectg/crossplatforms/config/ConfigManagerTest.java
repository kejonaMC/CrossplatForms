package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.TestLogger;
import dev.projectg.crossplatforms.action.SimpleAction;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.FormConfig;
import dev.projectg.crossplatforms.interfacing.bedrock.FormImageSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.ComponentSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

public class ConfigManagerTest {

    @TempDir
    private Path directory;

    @Test
    public void testBasicConfig() {
        TestLogger logger = new TestLogger(false);
        ConfigId root = new ConfigId("bedrock-forms.yml", FormConfig.VERSION, FormConfig.class);
        ConfigId nested = new ConfigId("configs/forms/bedrock-forms.yml", FormConfig.VERSION, FormConfig.class);

        ConfigManager manager = new ConfigManager(directory, logger);
        manager.serializers(builder -> {
            builder.registerExact(BedrockForm.class, new BedrockFormSerializer());
            builder.registerExact(FormImage.class, new FormImageSerializer());
            builder.registerExact(CustomComponent.class, new ComponentSerializer());
        });
        CrossplatForms.registerDefaultActions(manager);
        manager.getActionSerializer().registerSimpleType("server", String.class, FakeServer::new);

        manager.register(root);
        Assertions.assertTrue(manager.load());

        manager.register(nested);
        Assertions.assertTrue(manager.load());

        Assertions.assertFalse(logger.failed());
    }

    public static class FakeServer extends SimpleAction<String> {

        public FakeServer(@NotNull String value) {
            super("server", value);
        }

        @Override
        public void affectPlayer(@NotNull FormPlayer player, @NotNull Map<String, String> additionalPlaceholders, @NotNull InterfaceManager interfaceManager, @NotNull BedrockHandler bedrockHandler) {
            //no-op
        }
    }
}
