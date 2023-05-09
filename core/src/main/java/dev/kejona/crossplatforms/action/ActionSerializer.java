package dev.kejona.crossplatforms.action;

import dev.kejona.crossplatforms.serialize.TypeResolver;
import dev.kejona.crossplatforms.serialize.KeyedTypeSerializer;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class ActionSerializer {

    public static final TypeToken<Action<?>> TYPE = new TypeToken<Action<?>>() {};

    private final KeyedTypeSerializer<Action<?>> serializer = new KeyedTypeSerializer<>();

    public void register(String typeId, Class<? extends Action<?>> type) {
        serializer.registerType(typeId, type);
    }

    public void register(String typeId, Class<? extends Action<?>> type, TypeResolver resolver) {
        serializer.registerType(typeId, type, resolver);
    }

    public void register(TypeSerializerCollection.Builder builder) {
        builder.register(TYPE, serializer); // can't register exact because Action has type parameters
    }
}
