package dev.projectg.crossplatforms.serialize;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import dev.projectg.crossplatforms.Entry;
import dev.projectg.crossplatforms.utils.TypeUtils;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Deserializes a value node into {@link T} depending on the key of the node. This serializer must be registered using
 * {@link TypeSerializerCollection.Builder#registerExact(Class, TypeSerializer)} or
 * {@link TypeSerializerCollection.Builder#registerExact(TypeToken, TypeSerializer)} or
 * @param <T> A parent type that all possible deserializations of the node share
 */
public class KeyedTypeSerializer<T extends KeyedType> extends TypeRegistry<T> implements TypeSerializer<T> {

    private final Injector injector;
    private final Map<String, Entry<TypeToken<?>, Class<? extends T>>> simpleTypes = new HashMap<>();

    public KeyedTypeSerializer(Injector injector) {
        this.injector = injector;
    }

    @Override
    @Nonnull
    public Set<String> getTypes() {
        Set<String> types = new HashSet<>(simpleTypes.keySet());
        types.addAll(super.getTypes());
        return types;
    }

    public <V> void registerSimpleType(String typeId, TypeToken<V> valueType, Class<? extends T> simpleType) {
        String lowerCase = typeId.toLowerCase(Locale.ROOT);
        if (simpleTypes.get(lowerCase) != null) {
            throw new IllegalArgumentException("Simple Type " + lowerCase + " is already registered");
        }
        simpleTypes.put(lowerCase, Entry.of(valueType, simpleType));
    }

    public <V> void registerSimpleType(String typeId, Class<V> valueType, Class<? extends T> simpleType) {
        registerSimpleType(typeId, TypeToken.get(valueType), simpleType);
    }

    public T deserialize(ConfigurationNode node) throws SerializationException {
        Object key = node.key();
        if (key == null || key.toString().equals("")) {
            throw new SerializationException("Cannot deserialization a node into a KeyedType with a key of: " + key);
        }
        String typeId = key.toString();
        Class<? extends T> type = getType(typeId);

        T instance;
        if (type == null) {
            Entry<TypeToken<?>, Class<? extends T>> simpleType = simpleTypes.get(typeId.toLowerCase(Locale.ROOT));
            if (simpleType == null) {
                throw new SerializationException("Unsupported type (not registered) '" + typeId + "'. Possible options are: " + getTypes());
            } else {
                instance = deserializeSimple(simpleType.getKey(), simpleType.getValue(), node);
            }
        } else {
            instance = node.get(type);
        }
        return instance;    }

    @Override
    public T deserialize(Type returnType, ConfigurationNode node) throws SerializationException {
        return deserialize(node);
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

    private <V> T deserializeSimple(TypeToken<V> valueType, Class<? extends T> simpleType, ConfigurationNode node) throws SerializationException {
        final V value = node.get(valueType);
        Injector childInjector = injector.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(TypeUtils.keyFromToken(valueType)).toInstance(value);
            }
        });

        return childInjector.getInstance(simpleType);
    }
}
