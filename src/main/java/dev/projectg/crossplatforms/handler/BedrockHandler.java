package dev.projectg.crossplatforms.handler;

import org.geysermc.cumulus.Form;

import java.util.UUID;

public interface BedrockHandler {

    boolean isBedrockPlayer(UUID uuid);

    void sendForm(UUID uuid, Form form);

    int getPlayerCount();
}
