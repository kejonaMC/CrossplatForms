package dev.kejona.crossplatforms.serialize;

import org.spongepowered.configurate.ConfigurationNode;

/**
 * For use with {@link ValuedTypeSerializer}. Implementing classes must adhere to {@link ConfigurationNode#isMap()}
 * in order to be successfully serialized. They, do not need to be concerned with containing a type field,
 * as {@link ValuedTypeSerializer} handles the reading and writing of the type.
 *
 * However, the implementation of {@link ValuedType#type()} must be exactly equal to the String type identifier
 * provided in {@link ValuedTypeSerializer#registerType(String, Class)} when this implementation is registered.
 */
public interface ValuedType {

    String type();
}
