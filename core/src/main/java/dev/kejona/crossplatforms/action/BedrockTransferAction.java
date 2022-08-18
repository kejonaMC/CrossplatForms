package dev.kejona.crossplatforms.action;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.resolver.Resolver;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;

@ConfigSerializable
public class BedrockTransferAction implements Action<Object> {

    private static final String TYPE = "transfer_packet";

    private final transient BedrockHandler bedrockHandler;

    @Required
    private String address;

    @SuppressWarnings("FieldMayBeFinal")
    private String port = "19132";

    /**
     * To be used if port is an integer, can't change because of placeholders.
     */
    private transient int staticPort = -1;

    @Inject
    private BedrockTransferAction(BedrockHandler bedrockHandler) {
        this.bedrockHandler = bedrockHandler;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Resolver resolver, @Nonnull Object source) {
        if (!bedrockHandler.isBedrockPlayer(player.getUuid())) {
            player.warn("Attempted to transfer you to a server that supports Bedrock Edition but you aren't a Bedrock player.");
            Logger.get().warn("Can't use transfer_packet " + address + ":" + port + " on JE player " + player.getName());
            return;
        }

        String address = resolver.apply(this.address);

        int port;
        if (staticPort == -1) {
            // Failed to parse as int when action was first deserialized - assume its a placeholder.
            String resolved = resolver.apply(this.port);
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

        if (!bedrockHandler.transfer(player, address, port)) {
            player.warn("Failed to transfer you to a server due to an unknown reason");
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
        serializer.register(TYPE, BedrockTransferAction.class);
    }
}
