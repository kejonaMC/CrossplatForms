package dev.kejona.crossplatforms.config.valuedserializer;

import com.google.common.collect.ImmutableList;
import dev.kejona.crossplatforms.serialize.KeyedTypeSerializer;
import dev.kejona.crossplatforms.utils.ConfigurateUtils;
import dev.kejona.crossplatforms.utils.FileUtils;
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

public class KeyedTypeSerializerTest {

    private static final TypeToken<List<Number>> numberListType = new TypeToken<List<Number>>() {};

    @TempDir
    private static File directory;

    private final KeyedTypeSerializer<Number> numberSerializer = new KeyedTypeSerializer<>();
    private final YamlConfigurationLoader loader;

    public KeyedTypeSerializerTest() throws IOException {
        numberSerializer.registerType(Integer.TYPE, Integer.class);
        numberSerializer.registerType(ScientificNotationNumber.TYPE, ScientificNotationNumber.class);

        File config = FileUtils.fileOrCopiedFromResource(new File(directory, "ValuedTypeConfig.yml"));
        YamlConfigurationLoader.Builder loaderBuilder = ConfigurateUtils.loaderBuilder(config);
        loaderBuilder.defaultOptions(opts -> (opts.serializers(builder -> builder.registerExact(new TypeToken<Number>() {}, numberSerializer))));
        loader = loaderBuilder.build();
    }

    @Test
    public void testDeserialize() throws ConfigurateException {
        ConfigurationNode numbers = loader.load().node("numbers");
        Assertions.assertFalse(numbers.virtual());
        Assertions.assertTrue(numbers.isList());
        List<Number> actualMessages = numbers.get(numberListType);

        Integer integer = new Integer(5);
        ScientificNotationNumber scientific = new ScientificNotationNumber(7.200D, 3);
        List<Number> expectedMessages = ImmutableList.of(integer, scientific);

        Assertions.assertEquals(expectedMessages, actualMessages);
    }

    @Test
    public void testSerialize() throws ConfigurateException {
        ConfigurationNode numbers = loader.load().node("numbers");
        ConfigurationNode copy = numbers.copy();

        List<Number> actualNumbers = numbers.get(numberListType);
        Assertions.assertNotEquals(null, actualNumbers);
        Objects.requireNonNull(actualNumbers);

        copy.set(numberListType, actualNumbers);
        Assertions.assertEquals(numbers, copy);

        List<Number> modifiedMessages = new ArrayList<>(actualNumbers);
        modifiedMessages.add(new ScientificNotationNumber(3.141D, 0));
        copy.set(numberListType, modifiedMessages);
        Assertions.assertNotEquals(numbers, copy);
    }
}
