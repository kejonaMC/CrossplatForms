package dev.projectg.crossplatforms.action;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.Map;

@ConfigSerializable
public class BedrockTransferAction implements Action {

    public static final String TYPE = "transfer_packet";

    @Required
    private String address;

    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
    private int port = 19132;

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders, @Nonnull InterfaceManager interfaceManager, @Nonnull BedrockHandler bedrockHandler) {
        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            CrossplatForms.getInstance().getBedrockHandler().transfer(player, address, port);
        } else {
            player.sendMessage("Attempted to transfer you to a server that supports Bedrock Edition but you aren't a Bedrock player.");
            Logger.getLogger().warn("Can't use transfer_packet " + address + ":" + port + " on JE player " + player.getName());
        }
    }

    @Override
    public String type() {
        return TYPE;
    }
}
