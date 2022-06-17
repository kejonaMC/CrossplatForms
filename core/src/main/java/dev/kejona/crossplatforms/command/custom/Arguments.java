package dev.kejona.crossplatforms.command.custom;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

/**
 * Wrapper for {@link String[]} that properly implements {@link Object#equals(Object)} and {@link Object#hashCode()}.
 * The wrapped array will have a length of at least 1.
 */
public class Arguments {

    @Nonnull
    private final String[] source;

    public final int length;

    /**
     * @param arguments must be greater than 0.
     */
    private Arguments(@Nonnull String[] arguments) {
        this.source = Objects.requireNonNull(arguments);
        this.length = arguments.length;
        if (length < 1) {
            throw new IllegalArgumentException("Length was " + length + ", must be greater than 0");
        }
    }

    public static Arguments of(@Nonnull String[] arguments) {
        return new Arguments(arguments);
    }

    public String[] source() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arguments arguments = (Arguments) o;
        return Arrays.equals(source, arguments.source);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(source);
    }
}
