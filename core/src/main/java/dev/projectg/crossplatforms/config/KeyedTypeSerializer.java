package dev.projectg.crossplatforms.config;

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
public class KeyedTypeSerializer<T extends IdentifiableType> implements TypeSerializer<List<T>> {

    private final Map<String, Class<? extends T>> complexTypes = new HashMap<>();
    private final Map<String, SimpleTypeRegistration<?>> simpleTypes = new HashMap<>();

    /**
     * Register a type to be deserialized.
     * @param typeId The string identifier of the type, used as the map key
     * @param type The type of the map value. Must be serializable by Configurate
     */
    public void registerType(String typeId, Class<? extends T> type) {
        if (complexTypes.get(typeId) != null) {
            throw new IllegalArgumentException("Type " + typeId + " is already registered");
        }
        complexTypes.put(typeId, type);
    }

    /**
     * Register a simple type to be deserialized.
     * @param typeId The string identifier of the type, used as the map key
     * @param valueType The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     * @param creator A function that provides an instance of T given the map value.
     * @param <V> The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     */
    public <V> void registerSimpleType(String typeId, TypeToken<V> valueType, Function<V, T> creator) {
        if (simpleTypes.get(typeId) != null) {
            throw new IllegalArgumentException("Simple Type " + typeId + " is already registered");
        }
        simpleTypes.put(typeId, new SimpleTypeRegistration<>(valueType, creator));
    }

    /**
     * Register a simple type to be deserialized.
     * @param typeId The string identifier of the type, used as the map key
     * @param valueType The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     * @param creator A function that provides an instance of T given the map value.
     * @param <V> The type of the map value. There are no restrictions on this type, although it must be serializable by Configurate.
     */
    public <V> void registerSimpleType(String typeId, Class<V> valueType, Function<V, T> creator) {
        registerSimpleType(typeId, TypeToken.get(valueType), creator);
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

            Class<? extends T> type = this.complexTypes.get(key);
            if (type == null) {
                SimpleTypeRegistration<?> simpleType = simpleTypes.get(key);
                if (simpleType == null) {
                    throw new SerializationException("Unsupported type (not registered) <" + key + "> in " + node.path());
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
    public void serialize(Type type, @Nullable List<T> actions, ConfigurationNode node) throws SerializationException {
        if (actions == null) {
            node.raw(null);
            return;
        }

        for (T action : actions) {
            // action decides what value should be serialized. If it is a non-simple type, it is expected that the action
            // itself is passed. if its a simple typed, its expected that the singleton value is passed.
            node.node(action.identifier()).set(action.value());
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private class SimpleTypeRegistration<V> {
        private final TypeToken<V> valueType;
        private final Function<V, T> factory;

        protected T deserialize(ConfigurationNode node) throws SerializationException {
            return factory.apply(node.get(valueType));
        }
    }
}
