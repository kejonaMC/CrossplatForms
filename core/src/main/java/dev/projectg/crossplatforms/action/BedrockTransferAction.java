package dev.projectg.crossplatforms.action;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

@ConfigSerializable
public class BedrockTransferAction implements Action {

    public static final String TYPE = "transfer_packet";

    private final transient BedrockHandler bedrockHandler;
    private final transient PlaceholderHandler placeholders;

    @Required
    private String address;

    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
    private String port = "19132";

    /**
     * To be used if port is an integer, can't change because of placeholders.
     */
    private transient int staticPort = -1;

    private transient boolean checkedForStatic = false;

    @Inject
    private BedrockTransferAction(BedrockHandler bedrockHandler, PlaceholderHandler placeholders) {
        this.bedrockHandler = bedrockHandler;
        this.placeholders = placeholders;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders) {
        String actualAddress = placeholders.setPlaceholders(player, address, additionalPlaceholders);

        Integer actualPort = getPort(actualAddress, player, additionalPlaceholders);
        if (actualPort == null) {
            return; // messages handled in getPort
        }

        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            if (!bedrockHandler.transfer(player, actualAddress, actualPort)) {
                player.warn("Failed to transfer you to a server due to an unknown reason");
            }
        } else {
            player.warn("Attempted to transfer you to a server that supports Bedrock Edition but you aren't a Bedrock player.");
            Logger.get().warn("Can't use transfer_packet " + actualAddress + ":" + actualPort + " on JE player " + player.getName());
        }
    }

    @Nullable
    private Integer getPort(String address, FormPlayer player, Map<String, String> additionalPlaceholders) {
        // todo: fix this sin
        int actualPort;
        if (staticPort != -1) {
            // port is static integer
            return staticPort;
        } else {
            if (checkedForStatic) {
                // Already checked for static port and it is not
                String resolved = placeholders.setPlaceholders(player, port, additionalPlaceholders);
                try {
                    actualPort = Integer.parseUnsignedInt(resolved);
                } catch (NumberFormatException e) {
                    player.warn("Failed to transfer you to a server because " + resolved + " is not a valid port");
                    Logger.get().warn("Failed to send " + player.getName() + " to " + address + " because " + resolved + " is not a valid port");
                    return null;
                }
            } else {
                // This check should only occur once (lazy init)
                checkedForStatic = true; // set the check flag before we possibly call getPort again (to resolved placeholders)
                try {
                    actualPort = Integer.parseUnsignedInt(port);
                    staticPort = actualPort;
                } catch (NumberFormatException e) {
                    // config port is not an integer, try parsing placeholders
                    return getPort(address, player, additionalPlaceholders);
                }
            }
        }
        return actualPort;
    }

    @Override
    public String type() {
        return TYPE;
    }
}
