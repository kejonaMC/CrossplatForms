package dev.kejona.crossplatforms.spigot.adapter;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

public class VersionIndexResult {

    private final VersionAdapter adapter;
    private final String betterVersion;

    protected VersionIndexResult(@Nonnull VersionAdapter adapter, @Nonnull String betterVersion) {
        this.adapter = Objects.requireNonNull(adapter, "adapter");
        this.betterVersion = Objects.requireNonNull(betterVersion, "betterVersion");
    }

    protected VersionIndexResult(@Nonnull VersionAdapter adapter) {
        this.adapter = Objects.requireNonNull(adapter, "adapter");
        this.betterVersion = null;
    }

    protected VersionIndexResult(@Nonnull String betterVersion) {
        this.adapter = null;
        this.betterVersion = Objects.requireNonNull(betterVersion, "betterVersion");
    }

    public Optional<VersionAdapter> adapter() {
        return Optional.ofNullable(adapter);
    }

    public Optional<String> betterVersion() {
        return Optional.ofNullable(betterVersion);
    }
}
