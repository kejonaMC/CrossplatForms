package dev.projectg.crossplatforms.config.serializer;

import io.leangen.geantyref.TypeToken;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Deserializes a map with keys of type identifiers (strings) and values that are instances of type provided type T,
 * into a list of T.
 * @param <E> The parent type that all entry values have in common.
 */
public class KeyedTypeSerializer<E extends KeyedType> extends AbstractTypeSerializer<E> implements TypeSerializer<List<E>> {

    private final Map<String, SimpleTypeRegistration<?>> simpleTypes = new HashMap<>();

    /**
     * Register a simple type to be deserialized.
     *
     * @param typeId    The string identifier of the type, used as the map key
     * @param valueType The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     * @param creator   A function that provides an instance of T given the map value.
     * @param <V>       The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     */
    public <V> void registerSimpleType(String typeId, TypeToken<V> valueType, Function<V, E> creator) {
        if (simpleTypes.get(typeId) != null) {
            throw new IllegalArgumentException("Simple Type " + typeId + " is already registered");
        }
        simpleTypes.put(typeId, new SimpleTypeRegistration<>(valueType, creator));
    }

    /**
     * Register a simple type to be deserialized.
     *
     * @param typeId    The string identifier of the type, used as the map key
     * @param valueType The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     * @param creator   A function that provides an instance of T given the map value.
     * @param <V>       The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     */
    public <V> void registerSimpleType(String typeId, Class<V> valueType, Function<V, E> creator) {
        registerSimpleType(typeId, TypeToken.get(valueType), creator);
    }

    @Override
    public List<E> deserialize(Type returnType, ConfigurationNode node) throws SerializationException {
        Map<String, ConfigurationNode> childMap = node.get(new TypeToken<>() {});
        if (childMap == null) {
            throw new SerializationException("Map at " + node.path() + " is empty!");
        }

        List<E> mapped = new ArrayList<>();
        for (String typeId : childMap.keySet()) {
            ConfigurationNode entryNode = childMap.get(typeId);
            E instance;

            Class<? extends E> type = this.getTypes().get(typeId);
            if (type == null) {
                SimpleTypeRegistration<?> simpleType = simpleTypes.get(typeId);
                if (simpleType == null) {
                    throw new SerializationException("Unsupported type (not registered) <" + typeId + "> in " + node.path());
                } else {
                    instance = simpleType.deserialize(entryNode);
                }
            } else {
                instance = entryNode.get(type);
            }
            mapped.add(instance);
        }

        return mapped;
    }

    @Override
    public void serialize(Type type, @Nullable List<E> list, ConfigurationNode node) throws SerializationException {
        node.raw(null);

        if (list != null) {
            for (E action : list) {
                // action decides what value should be serialized. If it is a non-simple type, it is expected that the action
                // itself is passed. if its a simple typed, its expected that the singleton value is passed.
                node.node(action.identifier()).set(action.value());
            }
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private class SimpleTypeRegistration<V> {
        private final TypeToken<V> valueType;
        private final Function<V, E> factory;

        private E deserialize(ConfigurationNode node) throws SerializationException {
            // unfortunately a new TypeToken with type inference cannot be used to determine the type to deserialize with. the following seem to be related:
            // https://bugs.openjdk.java.net/browse/JDK-8267439
            // https://bugs.openjdk.java.net/browse/JDK-8262095
            return factory.apply(node.get(valueType));
        }
    }
}
