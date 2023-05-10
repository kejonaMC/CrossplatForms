package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.serialize.TypeResolver;
import dev.kejona.crossplatforms.serialize.KeyedTypeSerializer;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@Accessors(fluent = true)
@Getter
public class FillerSerializer {

    private final KeyedTypeSerializer<OptionFiller> optionFillerSerializer = new KeyedTypeSerializer<>();
    private final KeyedTypeSerializer<SimpleFormFiller> simpleFormFillerSerializer = new KeyedTypeSerializer<>();

    public <T extends UniversalFiller> void filler(String typeId, Class<T> fillerType) {
        optionFillerSerializer.registerType(typeId, fillerType);
        simpleFormFillerSerializer.registerType(typeId, fillerType);
    }

    public <T extends UniversalFiller> void filler(String typeId, Class<T> fillerType, TypeResolver resolver) {
        optionFillerSerializer.registerType(typeId, fillerType, resolver);
        simpleFormFillerSerializer.registerType(typeId, fillerType, resolver);
    }

    public void register(TypeSerializerCollection.Builder builder) {
        builder.registerExact(OptionFiller.class, optionFillerSerializer);
        builder.registerExact(SimpleFormFiller.class, simpleFormFillerSerializer);
    }
}
