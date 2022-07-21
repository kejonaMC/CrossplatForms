package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.serialize.ScalarSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class OptionSerializer extends ScalarSerializer<Option> {

    public OptionSerializer() {
        super(new TypeToken<Option>() {});
    }

    @Override
    public Option deserialize(Type type, Object obj) {
        return new Option(obj.toString());
    }

    @Override
    protected Object serialize(Option option, Predicate<Class<?>> typeSupported) {
        return option.display();
    }
}
