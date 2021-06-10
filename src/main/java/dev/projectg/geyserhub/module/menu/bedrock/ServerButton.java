package dev.projectg.geyserhub.module.menu.bedrock;

import org.geysermc.cumulus.util.FormImage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ServerButton extends Button{

    private final String serverName;

    ServerButton(@Nonnull String serverName, @Nonnull String text) {
        super(text);
        this.serverName = serverName;
    }

    ServerButton(@Nonnull String serverName, @Nonnull String text, @Nullable FormImage image) {
        super(text, image);
        this.serverName = serverName;
    }

    ServerButton(@Nonnull String serverName, @Nonnull String text, @Nonnull FormImage.Type type, @Nonnull String data) {
        super(text, type, data);
        this.serverName = serverName;
    }


    public String getServerName() {
        return serverName;
    }
}
