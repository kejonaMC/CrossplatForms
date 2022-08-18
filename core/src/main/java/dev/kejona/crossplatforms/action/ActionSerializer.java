package dev.kejona.crossplatforms.action;

import dev.kejona.crossplatforms.serialize.TypeResolver;
import dev.kejona.crossplatforms.serialize.ValuedTypeSerializer;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class ActionSerializer {

    public static final TypeToken<Action<?>> TYPE = new TypeToken<Action<?>>() {};

    private final ValuedTypeSerializer<Action<?>> genericActionSerializer = new ValuedTypeSerializer<>();

    public void register(String typeId, Class<? extends Action<?>> type) {
        genericActionSerializer.registerType(typeId, type);
    }

    public void register(String typeId, Class<? extends Action<?>> type, TypeResolver resolver) {
        genericActionSerializer.registerType(typeId, type, resolver);
    }

    public void register(TypeSerializerCollection.Builder builder) {
        builder.register(TYPE, genericActionSerializer); // can't register exact because Action has type parameters
    }
}
