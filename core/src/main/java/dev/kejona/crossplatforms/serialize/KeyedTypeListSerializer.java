package dev.kejona.crossplatforms.serialize;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Deserializes a map with keys of type identifiers (strings) and values that are instances of type provided type T,
 * into a list of T.
 * @param <E> The parent type that all entry values have in common.
 */
public class KeyedTypeListSerializer<E extends KeyedType> implements TypeSerializer<List<E>> {

    private final KeyedTypeSerializer<E> elementSerializer;

    public KeyedTypeListSerializer(KeyedTypeSerializer<E> elementSerializer) {
        this.elementSerializer = elementSerializer;
    }

    @Override
    public List<E> deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (!(type instanceof ParameterizedType)) {
            throw new SerializationException("Cannot deserialize to list with no element type parameter");
        }

        ParameterizedType parameterized = (ParameterizedType) type;
        Type[] typeArgs = parameterized.getActualTypeArguments();
        if (typeArgs.length < 1) {
            throw new SerializationException("Cannot deserialize to a list that is a raw type");
        }
        Type elementType = parameterized.getActualTypeArguments()[0];

        List<E> mapped = new ArrayList<>();
        for (ConfigurationNode child : node.childrenMap().values()) {
            mapped.add(elementSerializer.deserialize(elementType, child));
        }

        return mapped;
    }

    @Override
    public void serialize(Type type, @Nullable List<E> list, ConfigurationNode node) throws SerializationException {
        node.raw(null);

        if (list != null) {
            node.set(Collections.emptyMap());

            for (E element : list) {
                // KeyedType decides what value should be serialized. If it is a non-simple type, it is expected that
                // the class instance is passed. If its a simple type, its expected that the singleton value is passed.
                node.node(element.type()).set(element.value());
            }
        }
    }
}
