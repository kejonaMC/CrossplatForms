package dev.projectg.crossplatforms.config.form;

import com.google.inject.Guice;
import dev.projectg.crossplatforms.TestLogger;
import dev.projectg.crossplatforms.TestModule;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.command.DispatchableCommandSerializer;
import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.config.ConfigManagerTest;
import dev.projectg.crossplatforms.config.PrettyPrinter;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.FormConfig;
import dev.projectg.crossplatforms.interfacing.bedrock.FormImageSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.ComponentSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import dev.projectg.crossplatforms.utils.StringUtils;
import org.geysermc.cumulus.util.FormImage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

public class FormConfigUpdaterTest {

    private static final int OLD_VERSION = 1;
    private static final int CURRENT_VERSION = FormConfig.VERSION;

    @TempDir
    private static Path directory;

    private static final PrettyPrinter PRINTER = new PrettyPrinter(2, true);

    private final TestLogger logger = new TestLogger();
    private ConfigManager manager = null;

    public FormConfigUpdaterTest() {
        logger.setDebug(true);
    }

    @BeforeEach
    public void setupManager() {
        manager = new ConfigManager(directory, logger, Guice.createInjector(new TestModule()));
        // serializers
        manager.getActionSerializer().simpleGenericAction("server", String.class, ConfigManagerTest.FakeServer.class);
        manager.serializers(builder -> {
            builder.registerExact(DispatchableCommand.class, new DispatchableCommandSerializer());
            builder.registerExact(BedrockForm.class, new BedrockFormSerializer());
            builder.registerExact(FormImage.class, new FormImageSerializer());
            builder.registerExact(CustomComponent.class, new ComponentSerializer());
        });
    }

    @Test
    public void testAllVersions() {
        IntStream.range(OLD_VERSION, CURRENT_VERSION).forEachOrdered(this::testVersion);
    }

    public void testVersion(int version) {
        ConfigId config = id(version);
        manager.register(config);
        Assertions.assertTrue(manager.load());
        Assertions.assertFalse(logger.failed());
        System.out.println(StringUtils.repeatString("\n", 5));
        System.out.println(PRINTER.pretty(manager.getNode(FormConfig.class).orElseThrow(NoSuchElementException::new)));
    }

    private static ConfigId id(int version) {
        return new ConfigId("configs/forms/bedrock-forms-" + version + ".yml",
            CURRENT_VERSION, OLD_VERSION,
            FormConfig.class,
            FormConfig::updater);
    }
}
