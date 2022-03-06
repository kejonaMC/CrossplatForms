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

    @Override
    public T deserialize(Type returnType, ConfigurationNode node) throws SerializationException {
        String typeId = node.node(ValuedType.KEY).getString();
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
    public void serialize(Type type, @Nullable T value, ConfigurationNode node) throws SerializationException {
        node.raw(null);

        if (value != null) {
            String valueType = value.identifier();
            if (valueType == null || valueType.equals("")) {
                throw new SerializationException("Developer error: " + valueType + " has null or empty type field (child class of ValuedType)");
            }

            node.set(getType(valueType), value);
        }
    }
}
