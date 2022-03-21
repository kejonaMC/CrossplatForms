package dev.projectg.crossplatforms;

import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;

import javax.annotation.Nonnull;

public class BasicPlaceholders implements PlaceholderHandler {

    @Override
    public String setPlaceholders(@Nonnull FormPlayer player, @Nonnull String text) {
        if (text.isEmpty()) {
            return text;
        }
        return text.replace("%player_name%", player.getName()).replace("%player_uuid%", player.getUuid().toString());
    }
}
