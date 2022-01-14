package dev.projectg.crossplatforms.handler;

import org.geysermc.cumulus.Form;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.session.GeyserSession;

import java.util.UUID;

public class GeyserHandler implements BedrockHandler {

    @Override
    public boolean isBedrockPlayer(UUID uuid) {
        return GeyserImpl.getInstance().connectionByUuid(uuid) != null;
    }

    @Override
    public void sendForm(UUID uuid, Form form) {
        GeyserSession session = GeyserImpl.getInstance().connectionByUuid(uuid);
        if (session == null) {
            throw new NullPointerException("Failed to get GeyserSession for UUID " + uuid);
        } else {
            session.getFormCache().showForm(form);
        }
    }
}
