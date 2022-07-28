package dev.kejona.crossplatforms.action;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.Map;

@ConfigSerializable
public class BedrockTransferAction implements Action {

    private static final String TYPE = "transfer_packet";

    private final transient BedrockHandler bedrockHandler;
    private final transient Placeholders placeholders;

    @Required
    private String address;

    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
    private String port = "19132";

    /**
     * To be used if port is an integer, can't change because of placeholders.
     */
    private transient int staticPort = -1;

    @Inject
    private BedrockTransferAction(BedrockHandler bedrockHandler, Placeholders placeholders) {
        this.bedrockHandler = bedrockHandler;
        this.placeholders = placeholders;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders) {
        String address = placeholders.setPlaceholders(player, this.address, additionalPlaceholders);

        int port;
        if (staticPort == -1) {
            // Failed to parse as int when action was first deserialized - assume its a placeholder.
            String resolved = placeholders.setPlaceholders(player, this.port, additionalPlaceholders);
            try {
                port = Integer.parseUnsignedInt(resolved);
            } catch (NumberFormatException e) {
                player.warn("Failed to transfer you to a server because " + resolved + " is not a valid port");
                Logger.get().warn("Failed to send " + player.getName() + " to " + address + " because " + resolved + " is not a valid port");
                return;
            }
        } else {
            port = staticPort;
        }

        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            if (!bedrockHandler.transfer(player, address, port)) {
                player.warn("Failed to transfer you to a server due to an unknown reason");
            }
        } else {
            player.warn("Attempted to transfer you to a server that supports Bedrock Edition but you aren't a Bedrock player.");
            Logger.get().warn("Can't use transfer_packet " + address + ":" + port + " on JE player " + player.getName());
        }
    }

    @PostProcess
    private void postProcess() {
        try {
            staticPort = Integer.parseUnsignedInt(this.port);
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    public String type() {
        return TYPE;
    }

    public static void register(ActionSerializer serializer) {
        serializer.genericAction(TYPE, BedrockTransferAction.class);
    }
}
