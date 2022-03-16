package dev.projectg.crossplatforms.handler;

import com.nukkitx.protocol.bedrock.packet.TransferPacket;
import org.geysermc.cumulus.Form;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.session.GeyserSession;

import java.util.Objects;
import java.util.UUID;

public class GeyserHandler implements BedrockHandler {

    private final GeyserImpl geyser = Objects.requireNonNull(GeyserImpl.getInstance());

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
    public void transfer(FormPlayer player, String address, int port) {
        GeyserSession session = geyser.connectionByUuid(player.getUuid());
        if (session == null) {
            throw new IllegalArgumentException("Failed to find GeyserSession for " + player.getName() + player.getUuid());
        } else {
            TransferPacket packet = new TransferPacket();
            packet.setAddress(address);
            packet.setPort(port);
            session.sendUpstreamPacket(packet);
        }
    }
}
