package dev.projectg.crossplatforms.handler;

import org.geysermc.cumulus.Form;

import java.util.UUID;

/**
 * Used when Geyser nor Floodgate are installed. If this is used, modules that only handle Bedrock players should be disabled.
 */
public class EmptyBedrockHandler implements BedrockHandler {

    public static BedrockHandler INSTANCE = new EmptyBedrockHandler();

    @Override
    public boolean isBedrockPlayer(UUID uuid) {
        return false;
    }

    @Override
    public void sendForm(UUID uuid, Form form) {
        throw new AssertionError("Attempted to send a form with EmptyBedrockHandler");
    }

    @Override
    public int getPlayerCount() {
        return 0;
    }
}
