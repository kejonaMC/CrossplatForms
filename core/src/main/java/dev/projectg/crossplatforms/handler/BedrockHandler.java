package dev.projectg.crossplatforms.handler;

import org.geysermc.cumulus.Form;

import java.util.UUID;

public interface BedrockHandler {

    boolean isBedrockPlayer(UUID uuid);

    void sendForm(UUID uuid, Form form);

    /**
     * Transfer a bedrock player to a given server. You should check if the given {@link FormPlayer} is actually a Bedrock
     * player before calling this.
     * @param player The bedrock player to transfer
     * @param address The address of the new server to transfer to
     * @param port the port of the new server to transfer to
     */
    void transfer(FormPlayer player, String address, int port);

    static BedrockHandler empty() {
        return EmptyBedrockHandler.INSTANCE;
    }
}
