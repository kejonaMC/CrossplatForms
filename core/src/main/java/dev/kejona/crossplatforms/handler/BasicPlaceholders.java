package dev.projectg.crossplatforms.handler;

import javax.annotation.Nonnull;

public class BasicPlaceholders implements Placeholders {

    @Override
    public String setPlaceholders(@Nonnull FormPlayer player, @Nonnull String text) {
        if (text.isEmpty()) {
            return text;
        }
        return text.replace("%player_name%", player.getName()).replace("%player_uuid%", player.getUuid().toString());
    }
}
