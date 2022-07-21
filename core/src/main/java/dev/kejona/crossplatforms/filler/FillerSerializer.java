package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.serialize.ValuedTypeSerializer;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.function.Consumer;

@Accessors(fluent = true)
@Getter
public class FillerSerializer {

    private final ValuedTypeSerializer<OptionFiller> optionFillerSerializer = new ValuedTypeSerializer<>();
    private final ValuedTypeSerializer<SimpleFormFiller> simpleFormFillerSerializer = new ValuedTypeSerializer<>();
    private final ValuedTypeSerializer<InventoryFiller> inventoryFillerSerializer = new ValuedTypeSerializer<>();

    public Consumer<TypeSerializerCollection.Builder> registrator() {
        return builder -> {
            builder.registerExact(OptionFiller.class, optionFillerSerializer);
            builder.registerExact(SimpleFormFiller.class, simpleFormFillerSerializer);
            builder.registerExact(InventoryFiller.class, inventoryFillerSerializer);
        };
    }

    public <T extends UniversalFiller> void register(String typeId, Class<T> fillerType) {
        optionFillerSerializer.registerType(typeId, fillerType);
        simpleFormFillerSerializer.registerType(typeId, fillerType);
        inventoryFillerSerializer.registerType(typeId, fillerType);
    }
}
