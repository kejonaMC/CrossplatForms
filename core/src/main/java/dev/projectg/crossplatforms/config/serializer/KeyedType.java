package dev.projectg.crossplatforms.config.serializer;

/**
 * For use with {@link KeyedTypeSerializer}
 */
public interface KeyedType {

    /**
     * @return The identifier for the type of this implementation
     */
    String type();

    /**
     * @return An object serializable by Configurate that represents this implementation, which should be successfully
     * deserialized back to an equivalent instance of this implementation if necessary.
     *
     * The default implementation is that this returns the given instance that this is called on.
     */
    default Object value() {
        return this;
    }
}
