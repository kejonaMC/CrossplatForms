package dev.projectg.crossplatforms.config.serializer;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Deserializes a value {@link T} depending on a string node in the node representing {@link T}.
 * This serializer must be registered exact.
 * @param <T> The parent type that all entry values have in common.
 */
public class ValuedTypeSerializer<T extends ValuedType> extends TypeRegistry<T> implements TypeSerializer<T> {

    public static final String KEY = "type";

    @Override
    public void registerType(String typeId, Class<? extends T> type) {
        super.registerType(typeId, type);
    }

    @Override
    public T deserialize(Type returnType, ConfigurationNode node) throws SerializationException {
        String typeId = node.node(KEY).getString();
        if (typeId == null) {
            throw new SerializationException("Entry at " + node.path() + " does not contain a 'type' value. Possible options are: " + getTypes());
        }

        Class<? extends T> type = getType(typeId);
        if (type == null) {
            throw new SerializationException("Unsupported type (not registered) '" + typeId + "'. Possible options are: " + getTypes());
        }

        return node.get(type);
    }

    @Override
    public void serialize(Type returnType, @Nullable T value, ConfigurationNode node) throws SerializationException {
        node.raw(null);

        if (value != null) {
            String typeIdentifier = value.type();
            if (getType(typeIdentifier) == null) {
                // we don't actually need it for serializing but its probably a mistake or bad design
                throw new SerializationException("Cannot serialize implementation of ValueType " + value.getClass() + " that has not been registered");
            }

            node.set(value);
            node.node(KEY).set(typeIdentifier); // set it after to make sure its not overridden
        }
    }
}
