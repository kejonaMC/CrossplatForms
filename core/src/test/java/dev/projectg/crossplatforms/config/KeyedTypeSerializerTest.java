package dev.projectg.crossplatforms.config;

import com.google.common.collect.ImmutableList;
import dev.projectg.crossplatforms.action.KeyedTypeSerializer;
import dev.projectg.crossplatforms.utils.ConfigurateUtils;
import io.leangen.geantyref.TypeToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.util.List;

public class KeyedTypeSerializerTest {
    
    @Test
    public void testDeserialize() throws IOException {
        KeyedTypeSerializer<Message<?>> messageSerializer = new KeyedTypeSerializer<>();
        messageSerializer.registerSimpleType("message", String.class, MultipleMessages::new);
        messageSerializer.registerType("messages", MessageList.class);

        YamlConfigurationLoader.Builder loaderBuilder = ConfigurateUtils.loaderBuilder("KeyedTypeConfig.yml");
        loaderBuilder.defaultOptions(opts -> (opts.serializers(builder -> builder.register(new TypeToken<>() {}, messageSerializer))));
        loaderBuilder.nodeStyle(NodeStyle.BLOCK);
        loaderBuilder.indent(2);
        YamlConfigurationLoader loader = loaderBuilder.build();

        ConfigurationNode actions = loader.load().node("actions");
        Assertions.assertFalse(actions.virtual());
        Assertions.assertTrue(actions.isMap());
        List<Message<?>> actualMessages = actions.get(new TypeToken<>() {});

        MultipleMessages single = new MultipleMessages("[WARN] Hello");
        MessageList list = new MessageList("[INFO]", ImmutableList.of("One", "Two", "Three"));
        List<Message<?>> expectedMessages = ImmutableList.of(single, list);

        Assertions.assertEquals(expectedMessages, actualMessages);
    }
}
