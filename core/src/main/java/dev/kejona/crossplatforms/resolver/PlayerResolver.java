package dev.kejona.crossplatforms.resolver;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerResolver implements Resolver {

    private final FormPlayer player;
    private final Placeholders placeholders;

    @Override
    public String apply(String s) {
        if (s == null) {
            return null;
        }
        return placeholders.setPlaceholders(player, s);
    }
}
