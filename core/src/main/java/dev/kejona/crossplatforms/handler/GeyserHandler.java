package dev.kejona.crossplatforms.handler;

import org.geysermc.cumulus.form.Form;

import java.util.UUID;

public class GeyserHandler implements BedrockHandler {

    public GeyserHandler() {
        throw new UnsupportedOperationException("GeyserHandler is not supported on anything lower than Java 16");
    }

    public static boolean supported() {
        return false;
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
