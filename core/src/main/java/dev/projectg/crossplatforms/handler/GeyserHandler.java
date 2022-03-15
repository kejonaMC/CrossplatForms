package dev.projectg.crossplatforms.handler;

import org.geysermc.cumulus.Form;

import java.util.UUID;

public class GeyserHandler implements BedrockHandler {

    public GeyserHandler() {
        throw new UnsupportedOperationException("GeyserHandler is not supported on anything lower than Java 16");
    }

    @Override
    public boolean isBedrockPlayer(UUID uuid) {
        return false;
    }

    @Override
    public void sendForm(UUID uuid, Form form) {
        //no-op
    }

    @Override
    public void transfer(FormPlayer player, String address, int port) {
        //no-op
    }
}
