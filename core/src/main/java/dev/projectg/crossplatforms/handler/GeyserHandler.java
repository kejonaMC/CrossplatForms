package dev.projectg.crossplatforms.handler;

import org.geysermc.cumulus.Form;

import java.util.UUID;

public class GeyserHandler implements BedrockHandler {

    public static final boolean SUPPORTED = false;

    public GeyserHandler() {
        throw new UnsupportedOperationException("GeyserHandler is not supported on anything lower than Java 16");
    }

    @Override
    public String getType() {
        return "";
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
    public boolean executesResponseHandlersSafely() {
        return false;
    }

    @Override
    public boolean transfer(FormPlayer player, String address, int port) {
        return false;
    }
}
