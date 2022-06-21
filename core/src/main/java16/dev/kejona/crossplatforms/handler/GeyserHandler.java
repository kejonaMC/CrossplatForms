package dev.kejona.crossplatforms.handler;

import org.geysermc.api.session.Connection;
import org.geysermc.cumulus.form.Form;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.session.GeyserSession;

import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unused") // loaded on JDK 16
public class GeyserHandler implements BedrockHandler {

    private final GeyserImpl geyser = Objects.requireNonNull(GeyserImpl.getInstance());

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
        return geyser.connectionByUuid(uuid) != null;
    }

    @Override
    public void sendForm(UUID uuid, Form form) {
        GeyserSession session = geyser.connectionByUuid(uuid);
        if (session == null) {
            throw new NullPointerException("Failed to get GeyserSession for UUID " + uuid);
        } else {
            session.getFormCache().showForm(form);
        }
    }

    @Override
    public boolean executesResponseHandlersSafely() {
        return false;
    }

    @Override
    public boolean transfer(FormPlayer player, String address, int port) {
        Connection connection = geyser.connectionByUuid(player.getUuid());
        if (connection == null) {
            throw new IllegalArgumentException("Failed to find GeyserSession for " + player.getName() + player.getUuid());
        } else {
            return connection.transfer(address, port);
        }
    }
}
