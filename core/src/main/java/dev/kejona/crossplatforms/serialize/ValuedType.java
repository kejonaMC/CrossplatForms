package dev.kejona.crossplatforms.serialize;

import org.spongepowered.configurate.ConfigurationNode;

/**
 * For use with {@link ValuedTypeSerializer}. Implementing classes must adhere to {@link ConfigurationNode#isMap()}
 * in order to be successfully serialized. They do not need to be concerned with containing a type field,
 * as {@link ValuedTypeSerializer} handles the reading and writing of the type.
 * <p>
 * However, the implementation of {@link ValuedType#type()} must be exactly equal to the String type identifier
 * provided in {@link ValuedTypeSerializer#registerType(String, Class)} when this implementation is registered.
 */
public interface ValuedType {

    String type();

    /**
     * @return false if the type of a serialized form can always be inferred, meaning that the type does not
     * have to be included in the serialization. If it can sometimes or never be inferred, then true. {@link TypeResolver}s
     * that are registered to a {@link ValuedTypeSerializer} are responsible for inferring the type to deserialize as.
     * @see ValuedTypeSerializer#registerType(String, Class, TypeResolver)
     */
    default boolean serializeWithType() {
        return true;
    }
}
