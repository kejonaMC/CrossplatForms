package dev.kejona.crossplatforms.spigot.adapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class Versioned<T> {

    /**
     * The value found for a version
     */
    @Nullable
    private final T value;

    /**
     * A version that would be more ideal than the one used to find this value
     */
    @Nullable
    private final String betterVersion;

    private Versioned(@Nullable T value, @Nullable String betterVersion) {
        this.value = value;
        this.betterVersion = betterVersion;
    }

    public Optional<T> value() {
        return Optional.ofNullable(value);
    }

    public Optional<String> betterVersion() {
        return Optional.ofNullable(betterVersion);
    }

    public static <T> Versioned<T> supported(@Nonnull T value, @Nullable String betterVersion) {
        return new Versioned<>(Objects.requireNonNull(value), betterVersion);
    }

    public static <T> Versioned<T> supported(@Nonnull T value) {
        return supported(value, null);
    }

    public static <T> Versioned<T> unsupported(@Nonnull String betterVersion) {
        return new Versioned<>(null, Objects.requireNonNull(betterVersion));
    }

    /**
     * Converts the given {@link Versioned} of a {@link Supplier} by calling {@link Supplier#get()} if the value is
     * present, otherwise using null for the new Versioned.
     *
     * @param v The Supplier-type Versioned to convert
     * @param <T> The type that the Supplier provides, that will be the type of the converted VersionValue
     * @return A new {@link Versioned} with the Supplier intermediary removed
     */
    public static <T> Versioned<T> convertSupplierType(Versioned<Supplier<T>> v) {
        T value = v.value == null ? null : v.value.get();
        return new Versioned<>(value, v.betterVersion);
    }
}
