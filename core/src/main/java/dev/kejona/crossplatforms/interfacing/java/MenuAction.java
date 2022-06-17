package dev.kejona.crossplatforms.interfacing.java;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.serialize.KeyedType;

import javax.annotation.Nonnull;

public interface MenuAction extends KeyedType {

    /**
     * Affects a player
     * @param player The player to affect
     * @param menu The
     */
    void affectPlayer(@Nonnull FormPlayer player, @Nonnull JavaMenu menu);
}
