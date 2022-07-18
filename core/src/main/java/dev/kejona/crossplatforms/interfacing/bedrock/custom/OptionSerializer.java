package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class OptionSerializer extends ScalarSerializer<Option> {

    public OptionSerializer() {
        super(new TypeToken<Option>() {});
    }

    @Override
    public Option deserialize(Type type, Object obj) throws SerializationException {
        return new ConfigurateOption(obj.toString());
    }

    @Override
    protected Object serialize(Option entry, Predicate<Class<?>> typeSupported) {
        return entry.display();
    }
}
