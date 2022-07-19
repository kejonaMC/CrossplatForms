package dev.kejona.crossplatforms.config.form;

import com.google.inject.Guice;
import dev.kejona.crossplatforms.TestLogger;
import dev.kejona.crossplatforms.TestModule;
import dev.kejona.crossplatforms.command.DispatchableCommand;
import dev.kejona.crossplatforms.command.DispatchableCommandSerializer;
import dev.kejona.crossplatforms.config.ConfigId;
import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.config.ConfigManagerTest;
import dev.kejona.crossplatforms.config.PrettyPrinter;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockFormSerializer;
import dev.kejona.crossplatforms.interfacing.bedrock.FormConfig;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.ComponentSerializer;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import dev.kejona.crossplatforms.utils.StringUtils;
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
        return ConfigId.builder()
            .file("configs/forms/bedrock-forms-" + version + ".yml")
            .version(CURRENT_VERSION)
            .minimumVersion(OLD_VERSION)
            .clazz(FormConfig.class)
            .updater(FormConfig::updater)
            .build();
    }
}
