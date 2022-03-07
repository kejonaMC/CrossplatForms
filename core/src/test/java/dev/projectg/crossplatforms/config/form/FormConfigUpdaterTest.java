package dev.projectg.crossplatforms.config.form;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.TestLogger;
import dev.projectg.crossplatforms.action.SimpleAction;
import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.FormConfig;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

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
        manager.getActionSerializer().registerSimpleType("server", String.class, FakeServer::new);
        manager.register(oldest);
        manager.register(current);

        Assertions.assertTrue(manager.load());
        Assertions.assertEquals(manager.getNode(oldest.clazz), manager.getNode(current.clazz));
        Assertions.assertFalse(logger.failed());
    }

    private static ConfigId id(String name) {
        return new ConfigId("configs/forms/" + name, CURRENT_VERSION, OLD_VERSION, FormConfig.class, FormConfig::updater);
    }

    private static class FakeServer extends SimpleAction<String> {

        public FakeServer(@NotNull String value) {
            super(value);
        }

        @Override
        public void affectPlayer(@NotNull FormPlayer player, @NotNull Map<String, String> additionalPlaceholders, @NotNull InterfaceManager interfaceManager, @NotNull BedrockHandler bedrockHandler) {
            //no-op
        }

        @Override
        public String identifier() {
            return "server";
        }
    }
}
