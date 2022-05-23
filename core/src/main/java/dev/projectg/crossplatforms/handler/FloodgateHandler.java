package dev.projectg.crossplatforms.handler;

import dev.projectg.crossplatforms.Logger;
import org.geysermc.cumulus.Form;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public class FloodgateHandler implements BedrockHandler {

    private final FloodgateApi api;

    public FloodgateHandler() {
        api = FloodgateApi.getInstance();
    }

    @Override
    public String getType() {
        return "Floodgate";
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
    public boolean executesResponseHandlersSafely() {
        return true;
    }

    @Override
    public boolean transfer(FormPlayer player, String address, int port) {
        Logger.get().debug("Sending " + player.getName() + " to " + address + ":" + port);
        return api.transferPlayer(player.getUuid(), address, port);
    }
}
