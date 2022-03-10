package dev.projectg.crossplatforms;

import dev.projectg.crossplatforms.handler.FormPlayer;

import java.util.Collections;
import java.util.Map;
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
    public Map<String, Boolean> getPermissions() {
        return Collections.emptyMap();
    }

    @Override
    public void sendMessage(String message) {
        // no-op
    }

    @Override
    public Object getHandle() {
        return this;
    }
}
