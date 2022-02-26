package dev.projectg.crossplatforms.handler;

import org.geysermc.cumulus.Form;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.session.GeyserSession;

import java.util.UUID;

public class GeyserHandler implements BedrockHandler {

    private final GeyserImpl geyser;

    public GeyserHandler() {
        geyser = GeyserImpl.getInstance();
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
    public int getPlayerCount() {
        return geyser.getSessionManager().getAllSessions().size();
    }
}
