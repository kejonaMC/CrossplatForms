package dev.kejona.crossplatforms.handler;

import org.geysermc.cumulus.form.Form;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;

import java.util.UUID;

@SuppressWarnings("unused") // loaded on JDK 16
public class GeyserHandler implements BedrockHandler {

    private final GeyserApi api = GeyserApi.api();

    public static boolean supported() {
        // method is used so that compiler doesn't inline value
        return true;
    }

    @Override
    public String getType() {
        return "Geyser";
    }

    @Override
    public boolean isBedrockPlayer(UUID uuid) {
        return api.isBedrockPlayer(uuid);
    }

    @Override
    public void sendForm(UUID uuid, Form form) {
        // don't use GeyserApi#sendForm since it only returns false when there is no session found for the uuid
        requireConnection(uuid).sendForm(form);
    }

    @Override
    public boolean executesResponseHandlersSafely() {
        return false;
    }

    @Override
    public boolean transfer(FormPlayer player, String address, int port) {
        // don't use GeyserApi#transfer since it only returns false when there is no session found for the uuid
        return requireConnection(player.getUuid()).transfer(address, port);
    }

    private GeyserConnection requireConnection(UUID uuid) {
        GeyserConnection connection = api.connectionByUuid(uuid);
        if (connection == null) {
            throw new NullPointerException("Failed to get GeyserConnection for UUID: " + uuid);
        }

        return connection;
    }
}
