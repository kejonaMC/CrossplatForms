package dev.kejona.crossplatforms.serialize;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Deserializes a value {@link T} depending on a string node within the node representing {@link T}.
 * This serializer must be registered with one of the registerExact methods of {@link TypeSerializerCollection.Builder}
 * @param <T> The parent type that all entry values have in common.
 */
public class KeyedTypeSerializer<T extends KeyedType> extends TypeRegistry<T> implements TypeSerializer<T> {

    private final String typeKey;
    private final List<TypeResolver> typeResolvers = new ArrayList<>();

    /**
     * Creates a ValuedTypeSerializer with the given key to read the type at
     * @param typeKey The key that the type value is expected to reside at when deserializing/serializing
     */
    public KeyedTypeSerializer(String typeKey) {
        this.typeKey = Objects.requireNonNull(typeKey);
    }

    /**
     * Creates a ValuedTypeSerializer with a type key of "type".
     */
    public KeyedTypeSerializer() {
        this("type");
    }

    public void registerType(String typeId, Class<? extends T> type, TypeResolver typeResolver) {
        registerType(typeId, type);
        typeResolvers.add(typeResolver);
    }

    @Override
    public T deserialize(Type returnType, ConfigurationNode node) throws SerializationException {
        String typeId = node.node(typeKey).getString();
        if (typeId == null) {
            // try to infer the type based off the nodes content
            for (TypeResolver resolver : typeResolvers) {
                String possibleType = resolver.inferType(node);
                if (possibleType != null) {
                    if (typeId != null) {
                        throw new SerializationException("Failed to infer the type because both types matched: " + typeId + " and " + possibleType);
                    }
                    typeId = possibleType;
                }
            }

            if (typeId == null) {
                throw new SerializationException("No 'type' value present and the type could not be inferred. Possible type options are: " + getTypes(returnType));
            }
        }

        Class<? extends T> type = getType(typeId);
        if (type == null) {
            throw new SerializationException("Unsupported type '" + typeId + "'. Not registered. Possible options are: " + getTypes(returnType));
        }

        if (!isCompatible(returnType, type)) {
            throw new SerializationException("Unsupported type '" + typeId + "'. " + type + " is registered but incompatible. Possible type options are: " + getTypes(returnType));
        }

        T object = node.get(type);
        if (object == null) {
            throw new SerializationException("Failed to deserialize as '" + type + "' because deserialization returned null.");
        }

        return object;
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
            if (value.serializeWithType()) {
                // the type must be included because when this node is deserialized, the type cannot be inferred
                node.node(typeKey).set(typeIdentifier);
            }
        }
    }
}
