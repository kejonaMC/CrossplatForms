package dev.projectg.crossplatforms.config.valuedserializer;

import com.google.common.collect.ImmutableList;
import dev.projectg.crossplatforms.config.serializer.ValuedTypeSerializer;
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

public class ValuedTypeSerializerTest {

    private final ValuedTypeSerializer<Number> numberSerializer = new ValuedTypeSerializer<>();
    private final YamlConfigurationLoader loader;

    @TempDir
    private static File directory;

    public ValuedTypeSerializerTest() throws IOException {
        numberSerializer.registerType("integer", Integer.class);
        numberSerializer.registerType("scientific_notation", ScientificNotationNumber.class);

        File config = FileUtils.fileOrCopiedFromResource(new File(directory, "ValuedTypeConfig.yml"));
        YamlConfigurationLoader.Builder loaderBuilder = ConfigurateUtils.loaderBuilder(config);
        loaderBuilder.defaultOptions(opts -> (opts.serializers(builder -> builder.register(new TypeToken<>() {}, numberSerializer))));
        loader = loaderBuilder.build();
    }

    @Test
    public void testDeserialize() throws ConfigurateException {
        ConfigurationNode numbers = loader.load().node("numbers");
        Assertions.assertFalse(numbers.virtual());
        Assertions.assertTrue(numbers.isList());
        List<Number> actualMessages = numbers.get(new TypeToken<>() {
        });

        Integer integer = new Integer(5);
        ScientificNotationNumber scientific = new ScientificNotationNumber(7.200D, 3);
        List<Number> expectedMessages = ImmutableList.of(integer, scientific);

        Assertions.assertEquals(expectedMessages, actualMessages);
    }

    @Test
    public void testSerialize() throws ConfigurateException {
        ConfigurationNode numbers = loader.load().node("numbers");
        ConfigurationNode copy = numbers.copy();

        List<Number> actualNumbers = numbers.get(new TypeToken<>() {});
        Assertions.assertNotEquals(null, actualNumbers);
        Objects.requireNonNull(actualNumbers);

        copy.set(new TypeToken<>() {}, actualNumbers);
        Assertions.assertEquals(numbers, copy);

        List<Number> modifiedMessages = new ArrayList<>(actualNumbers);
        modifiedMessages.add(new ScientificNotationNumber(3.141D, 0));
        copy.set(new TypeToken<>() {}, modifiedMessages);
        Assertions.assertNotEquals(numbers, copy);
    }
}
