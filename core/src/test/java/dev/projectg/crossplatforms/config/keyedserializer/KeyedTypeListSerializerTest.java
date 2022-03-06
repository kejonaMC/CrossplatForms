package dev.projectg.crossplatforms.config.keyedserializer;

import com.google.common.collect.ImmutableList;
import dev.projectg.crossplatforms.config.serializer.KeyedTypeListSerializer;
import dev.projectg.crossplatforms.config.serializer.KeyedTypeSerializer;
import dev.projectg.crossplatforms.utils.ConfigurateUtils;
import dev.projectg.crossplatforms.utils.FileUtils;
import io.leangen.geantyref.TypeToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KeyedTypeListSerializerTest {

    private final KeyedTypeSerializer<Message> messageSerializer = new KeyedTypeSerializer<>();
    private final YamlConfigurationLoader loader;

    @TempDir
    private static File directory;

    public KeyedTypeListSerializerTest() throws IOException {
        messageSerializer.registerSimpleType("message", String.class, SingleMessage::new);
        messageSerializer.registerType("messages", MultiMessage.class);

        File config = FileUtils.fileOrCopiedFromResource(new File(directory, "KeyedTypeConfig.yml"));
        YamlConfigurationLoader.Builder loaderBuilder = ConfigurateUtils.loaderBuilder(config);
        loaderBuilder.defaultOptions(opts -> (opts.serializers(builder -> {
            builder.registerExact(Message.class, messageSerializer);
            builder.register(new TypeToken<>() {}, new KeyedTypeListSerializer<>(messageSerializer));
        })));
        loader = loaderBuilder.build();
    }
    
    @Test
    public void testDeserialize() throws ConfigurateException {
        ConfigurationNode actions = loader.load().node("actions");
        Assertions.assertFalse(actions.virtual());
        Assertions.assertTrue(actions.isMap());
        List<Message> actualMessages = actions.get(new TypeToken<>() {});

        SingleMessage single = new SingleMessage("[WARN] Hello");
        MultiMessage list = new MultiMessage("[INFO]", ImmutableList.of("One", "Two", "Three"));
        List<Message> expectedMessages = ImmutableList.of(single, list);

        Assertions.assertEquals(expectedMessages, actualMessages);
    }

    @Test
    public void testSerialize() throws ConfigurateException {
        ConfigurationNode actions = loader.load().node("actions");
        ConfigurationNode copy = actions.copy();

        List<Message> actualMessages = actions.get(new TypeToken<>() {});
        Objects.requireNonNull(actualMessages);

        copy.set(new TypeToken<>() {}, actualMessages);
        Assertions.assertEquals(actions, copy);

        List<Message> modifiedMessages = new ArrayList<>(actualMessages);
        modifiedMessages.add(new SingleMessage("greetings"));
        copy.set(new TypeToken<>() {}, modifiedMessages);
        Assertions.assertNotEquals(actions, copy);
    }
}
