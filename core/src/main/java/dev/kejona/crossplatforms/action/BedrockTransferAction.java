package dev.kejona.crossplatforms.action;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.resolver.Resolver;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;

@ConfigSerializable
public class BedrockTransferAction implements GenericAction {

    private static final String TYPE = "transfer_packet";

    private final transient BedrockHandler bedrockHandler;

    @Required
    private String address;

    @SuppressWarnings("FieldMayBeFinal")
    private String port = "19132";

    @Inject
    private BedrockTransferAction(BedrockHandler bedrockHandler) {
        this.bedrockHandler = bedrockHandler;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Resolver resolver) {
        if (!bedrockHandler.isBedrockPlayer(player.getUuid())) {
            player.warn("Attempted to transfer you to a server that supports Bedrock Edition but you aren't a Bedrock player.");
            Logger.get().warn("Can't use transfer_packet " + address + ":" + port + " on JE player " + player.getName());
            return;
        }

        String address = resolver.apply(this.address);

        int port;
        String resolved = resolver.apply(this.port);
        try {
            port = Integer.parseUnsignedInt(resolved);
        } catch (NumberFormatException e) {
            player.warn("Failed to transfer you to a server because there was an error determining the destination.");
            warn(player, address, resolved, resolved + " is not a valid port.");
            return;
        }

        if (!bedrockHandler.transfer(player, address, port)) {
            player.warn("Failed to transfer you to a server due to an unknown reason");
            warn(player, address, resolved, " of an unknown reason.");
        }
    }

    private static void warn(FormPlayer player, String address, String port, String reason) {
        Logger.get().warn("Failed to send " + player.getName() + " to " + address + ":" + port + " because " + reason);
    }

    @Override
    public String type() {
        return TYPE;
    }

    public static void register(ActionSerializer serializer) {
        serializer.register(TYPE, BedrockTransferAction.class);
    }
}
