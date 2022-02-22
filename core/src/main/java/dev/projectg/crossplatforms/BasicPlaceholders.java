package dev.projectg.crossplatforms;

import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import org.jetbrains.annotations.NotNull;

public class BasicPlaceholders implements PlaceholderHandler {

    @Override
    public String setPlaceholders(@NotNull FormPlayer player, @NotNull String text) {
        if (text.isBlank()) {
            return text;
        }
        return text.replace("%player_name%", player.getName()).replace("%player_uuid%", player.getUuid().toString());
    }
}
