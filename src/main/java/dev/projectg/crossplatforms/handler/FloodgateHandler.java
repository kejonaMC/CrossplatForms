package dev.projectg.crossplatforms.handler;

import org.geysermc.cumulus.Form;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public class FloodgateHandler implements BedrockHandler {

    @Override
    public boolean isBedrockPlayer(UUID uuid) {
        return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    }

    @Override
    public void sendForm(UUID uuid, Form form) {
        FloodgateApi.getInstance().sendForm(uuid, form);
    }
}
