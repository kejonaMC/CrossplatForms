package dev.projectg.crossplatforms.handler;

import org.geysermc.cumulus.Form;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public class FloodgateHandler implements BedrockHandler {

    private final FloodgateApi api;

    public FloodgateHandler() {
        api = FloodgateApi.getInstance();
    }

    @Override
    public boolean isBedrockPlayer(UUID uuid) {
        return api.isFloodgatePlayer(uuid);
    }

    @Override
    public void sendForm(UUID uuid, Form form) {
        api.sendForm(uuid, form);
    }

    @Override
    public void transfer(FormPlayer player, String address, int port) {
        api.transferPlayer(player.getUuid(), address, port);
    }
}
