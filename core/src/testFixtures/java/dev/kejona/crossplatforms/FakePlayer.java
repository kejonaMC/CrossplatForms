package dev.kejona.crossplatforms;

import dev.kejona.crossplatforms.handler.FormPlayer;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class FakePlayer implements FormPlayer {

    private final UUID uuid = UUID.randomUUID();

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getName() {
        return "FakePlayer";
    }

    @Override
    public boolean hasPermission(String permission) {
        return false;
    }

    @Override
    public void sendRaw(Component component) {
        // no-op
    }

    @Override
    public boolean switchBackendServer(String server) {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getHandle(Class<T> asType) throws ClassCastException {
        return (T) this;
    }
}
