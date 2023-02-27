package dev.kejona.crossplatforms.spigot.adapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class Versioned<T> {

    /**
     * The value found for a version
     */
    private final T value;

    /**
     * A version that would be more ideal than the one used to find this value
     */
    private final String betterVersion;

    public Versioned(@Nullable T value, @Nullable String betterVersion) {
        this.value = value;
        this.betterVersion = betterVersion;
    }

    public Versioned(@Nonnull T value) {
        this(value, null);
    }

    public Versioned(@Nonnull String betterVersion) {
        this(null, betterVersion);
    }

    public Optional<T> value() {
        return Optional.ofNullable(value);
    }

    public Optional<String> betterVersion() {
        return Optional.ofNullable(betterVersion);
    }

    /**
     * Converts the given {@link Versioned} of a {@link Supplier} by calling {@link Supplier#get()} if the value is
     * present, otherwise using null for the new Versioned.
     *
     * @param versioned The Supplier-type Versioned to convert
     * @param <T> The type that the Supplier provides, that will be the type of the converted VersionValue
     * @return A new {@link Versioned} with the Supplier intermediary removed
     */
    public static <T> Versioned<T> convertSupplierType(Versioned<Supplier<T>> versioned) {
        return new Versioned<>(
            versioned.value().map(Supplier::get).orElse(null),
            versioned.betterVersion().orElse(null)
        );
    }
}
