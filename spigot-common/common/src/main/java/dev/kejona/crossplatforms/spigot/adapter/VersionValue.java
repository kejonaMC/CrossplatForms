package dev.kejona.crossplatforms.spigot.adapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class VersionValue<T> {

    /**
     * The value found for a version
     */
    private final T value;

    /**
     * A version that would be more ideal than the one used to find this value
     */
    private final String betterVersion;

    public VersionValue(@Nullable T value, @Nullable String betterVersion) {
        this.value = value;
        this.betterVersion = betterVersion;
    }

    public VersionValue(@Nonnull T value) {
        this(value, null);
    }

    public VersionValue(@Nonnull String betterVersion) {
        this(null, betterVersion);
    }

    public Optional<T> value() {
        return Optional.ofNullable(value);
    }

    public Optional<String> betterVersion() {
        return Optional.ofNullable(betterVersion);
    }

    /**
     * Converts the given {@link Supplier} type {@link VersionValue} by calling {@link Supplier#get()} if the value is
     * present, otherwise using null for the new VersionValue.
     *
     * @param value The Supplier type VersionValue to convert
     * @param <T> The type that the Supplier provides, that will be the type of the converted VersionValue
     * @return A new {@link VersionValue} with the Supplier intermediary removed
     */
    public static <T> VersionValue<T> convertSupplierType(VersionValue<Supplier<T>> value) {
        return new VersionValue<>(
            value.value().map(Supplier::get).orElse(null),
            value.betterVersion().orElse(null)
        );
    }
}
