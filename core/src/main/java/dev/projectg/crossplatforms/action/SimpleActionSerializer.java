package dev.projectg.crossplatforms.action;

import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class SimpleActionSerializer<T> implements TypeSerializer<SimpleAction<T>> {

    @Override
    public SimpleAction<T> deserialize(Type type, ConfigurationNode node) throws SerializationException {
        T value = node.get(new TypeToken<>() {});
        return null;
    }

    @Override
    public void serialize(Type type, @Nullable SimpleAction<T> obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }
    }
}
