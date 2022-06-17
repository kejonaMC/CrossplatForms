package dev.kejona.crossplatforms.action;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;
import dev.kejona.crossplatforms.interfacing.java.MenuAction;
import dev.kejona.crossplatforms.serialize.KeyedType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

public interface Action extends KeyedType, MenuAction {

    /**
     * Affects a player
     * @param player The player to affect
     */
    default void affectPlayer(@Nonnull FormPlayer player) {
        affectPlayer(player, Collections.emptyMap());
    }

    /**
     * Affects a player
     * @param player The player to affect
     * @param additionalPlaceholders Additional placeholders to resolve
     */
    void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders);

    @Override
    default void affectPlayer(@Nonnull FormPlayer player, @Nonnull JavaMenu menu) {
        affectPlayer(player);
    }

    // Static methods for batching multiple actions together:

    static void affectPlayer(@Nonnull FormPlayer player,
                             @Nonnull Iterable<Action> actions,
                             @Nonnull Map<String, String> additionalPlaceholders) {

        actions.forEach(a -> a.affectPlayer(player, additionalPlaceholders));
    }

    static void affectPlayer(@Nonnull FormPlayer player,
                             @Nonnull Iterable<Action> actions) {

        actions.forEach(a -> a.affectPlayer(player, Collections.emptyMap()));
    }
}
