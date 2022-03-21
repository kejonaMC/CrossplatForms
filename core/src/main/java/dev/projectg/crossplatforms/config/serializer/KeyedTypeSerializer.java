package dev.projectg.crossplatforms.config.serializer;

import io.leangen.geantyref.TypeToken;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Deserializes a value node into {@link T} depending on the key of the node. This serializer must be registered using
 * {@link TypeSerializerCollection.Builder#registerExact(Class, TypeSerializer)} or
 * {@link TypeSerializerCollection.Builder#registerExact(TypeToken, TypeSerializer)} or
 * @param <T> A parent type that all possible deserializations of the node share
 */
public class KeyedTypeSerializer<T extends KeyedType> extends TypeRegistry<T> implements TypeSerializer<T> {

    private final Map<String, SimpleTypeRegistration<?>> simpleTypes = new HashMap<>();

    /**
     * Register a simple type to be deserialized.
     *
     * @param typeId    The string identifier of the type, used as the map key
     * @param valueType The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     * @param creator   A function that provides an instance of T given the map value.
     * @param <V>       The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     */
    public <V> void registerSimpleType(String typeId, TypeToken<V> valueType, Function<V, T> creator) {
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
    public <V> void registerSimpleType(String typeId, Class<V> valueType, Function<V, T> creator) {
        registerSimpleType(typeId, TypeToken.get(valueType), creator);
    }

    @Override
    public T deserialize(@Nullable Type returnType, ConfigurationNode node) throws SerializationException {
        Object key = node.key();
        if (key == null || key.toString().equals("")) {
            throw new SerializationException("Cannot deserialization a node into a KeyedType with a key of: " + key);
        }
        String typeId = key.toString();
        Class<? extends T> type = getType(typeId);

        T instance;
        if (type == null) {
            SimpleTypeRegistration<?> simpleType = simpleTypes.get(typeId);
            if (simpleType == null) {
                throw new SerializationException("Unsupported type (not registered) '" + typeId + "'");
            } else {
                instance = simpleType.deserialize(node);
            }
        } else {
            instance = node.get(type);
        }
        return instance;
    }

    @Override
    public void serialize(Type type, @Nullable T keyedType, ConfigurationNode node) throws SerializationException {
        node.raw(null);

        if (keyedType != null) {
            if (keyedType.type().equals(node.key())) {
                node.set(keyedType.value());
            } else {
                throw new SerializationException("Cannot deserialize '" + keyedType.value() + "' of type '" + keyedType.type() + "' because the key of the node at " + node.path() + " does not match the type");
            }
        }
    }

    @AllArgsConstructor
    private class SimpleTypeRegistration<V> {
        private final TypeToken<V> valueType;
        private final Function<V, T> factory;

        private T deserialize(ConfigurationNode node) throws SerializationException {
            // unfortunately a new TypeToken with type inference cannot be used to determine the type to deserialize with. the following seem to be related:
            // https://bugs.openjdk.java.net/browse/JDK-8267439
            // https://bugs.openjdk.java.net/browse/JDK-8262095
            return factory.apply(node.get(valueType));
        }
    }
}
