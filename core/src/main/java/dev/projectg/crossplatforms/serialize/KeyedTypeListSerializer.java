package dev.projectg.crossplatforms.serialize;

import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Map<String, ConfigurationNode> childMap = node.get(new TypeToken<Map<String, ConfigurationNode>>() {});
        if (childMap == null) {
            throw new SerializationException("Map at " + node.path() + " is empty or not the correct type!");
        }

        List<E> mapped = new ArrayList<>();
        for (ConfigurationNode child : childMap.values()) {
            mapped.add(elementSerializer.deserialize(null, child));
        }

        return mapped;
    }

    @Override
    public void serialize(Type type, @Nullable List<E> list, ConfigurationNode node) throws SerializationException {
        node.raw(null);

        if (list != null) {
            for (E element : list) {
                // action decides what value should be serialized. If it is a non-simple type, it is expected that the action
                // itself is passed. if its a simple typed, its expected that the singleton value is passed.
                node.node(element.type()).set(element.value());
            }
        }
    }
}
