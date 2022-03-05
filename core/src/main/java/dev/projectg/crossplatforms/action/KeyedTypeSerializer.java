package dev.projectg.crossplatforms.action;

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
 * @param <T> The parent type that all entry values have in common.
 */
public class KeyedTypeSerializer<T> implements TypeSerializer<List<T>> {

    private final Map<String, Class<? extends T>> types = new HashMap<>();
    private final Map<String, SimpleTypeRegistration<?>> simpleTypes = new HashMap<>();

    /**
     * Register a type to be deserialized.
     * @param typeId The string identifier of the type, used as the map key
     * @param type The type of the map value. Must be serializable by Configurate
     */
    public void registerType(String typeId, Class<? extends T> type) {
        if (types.get(typeId) != null) {
            throw new IllegalArgumentException("Type " + typeId + " is already registered");
        }
        types.put(typeId, type);
    }

    /**
     * Register a simple type to be deserialized.
     * @param typeId The string identifier of the type, used as the map key
     * @param type The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     * @param creator A function that provides an instance of T given the map value.
     * @param <V> The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     */
    public <V> void registerSimpleType(String typeId, TypeToken<V> type, Function<V, T> creator) {
        if (simpleTypes.get(typeId) != null) {
            throw new IllegalArgumentException("Simple Type " + typeId + " is already registered");
        }
        simpleTypes.put(typeId, new SimpleTypeRegistration<>(type, creator));
    }

    /**
     * Register a simple type to be deserialized.
     * @param typeId The string identifier of the type, used as the map key
     * @param type The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     * @param creator A function that provides an instance of T given the map value.
     * @param <V> The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     */
    public <V> void registerSimpleType(String typeId, Class<V> type, Function<V, T> creator) {
        if (simpleTypes.get(typeId) != null) {
            throw new IllegalArgumentException("Simple Type " + typeId + " is already registered");
        }
        registerSimpleType(typeId, TypeToken.get(type), creator);
    }

    @Override
    public List<T> deserialize(Type returnType, ConfigurationNode node) throws SerializationException {
        Map<String, ConfigurationNode> childMap = node.get(new TypeToken<>() {});
        if (childMap == null) {
            throw new SerializationException("Map at " + node.path() + " is empty!");
        }

        List<T> mapped = new ArrayList<>();
        for (Map.Entry<String, ConfigurationNode> entry : childMap.entrySet()) {
            String key = entry.getKey();
            ConfigurationNode entryNode = entry.getValue();
            T instance;

            Class<? extends T> type = this.types.get(key);
            if (type == null) {
                SimpleTypeRegistration<?> simpleType = simpleTypes.get(key);
                if (simpleType == null) {
                    throw new SerializationException("Unsupported type (not registered) <" + key + "> in " + node.path());
                } else {
                    instance = simpleType.create(entryNode);
                }
            } else {
                instance = entryNode.get(type);
            }
            mapped.add(instance);
        }

        return mapped;
    }

    @Override
    public void serialize(Type type, @Nullable List<T> actions, ConfigurationNode node) throws SerializationException {
        if (actions == null) {
            node.raw(null);
            return;
        }

        node.set(new TypeToken<>() {}, actions); //todo: probably broken
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private class SimpleTypeRegistration<V> {
        private final TypeToken<V> type;
        private final Function<V, T> factory;

        protected T create(ConfigurationNode node) throws SerializationException {
            return factory.apply(node.get(type));
        }
    }
}
