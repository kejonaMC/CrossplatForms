package dev.kejona.crossplatforms.command.custom;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * Wrapper for {@link String[]} that properly implements {@link Object#equals(Object)} and {@link Object#hashCode()}.
 * The wrapped array will have a length of at least 1.
 */
public class Literals {

    @Nonnull
    private final String[] source;

    public final int length;

    /**
     * @param arguments must be greater than 0.
     */
    private Literals(@Nonnull String[] arguments) {
        this.source = Objects.requireNonNull(arguments);
        this.length = arguments.length;
        if (length < 1) {
            throw new IllegalArgumentException("Length was " + length + ", must be greater than 0");
        }
    }

    public static Literals of(@Nonnull String[] arguments) {
        return new Literals(arguments);
    }

    public String[] source() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Literals literals = (Literals) o;
        return Arrays.equals(source, literals.source);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(source);
    }

    public static class Serializer implements TypeSerializer<Literals> {

        @Override
        public Literals deserialize(Type type, ConfigurationNode node) throws SerializationException {
            String joined = node.getString();
            if (joined == null) {
                throw new SerializationException("Command arguments must be a string");
            }
            String[] separated = joined.split(" ");
            if (separated.length < 1) {
                throw new SerializationException("Command arguments length was " + separated.length + ", needs to be greater than 0.");
            }
            return of(separated);
        }

        @Override
        public void serialize(Type type, @Nullable Literals obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) {
                node.raw(null);
                return;
            }
            String joined = String.join(" ", obj.source());
            node.set(String.class, joined);
        }
    }
}
