package dev.projectg.crossplatforms.interfacing.java;

import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.serialize.KeyedType;

import javax.annotation.Nonnull;

public interface MenuAction extends KeyedType {

    /**
     * Affects a player
     * @param player The player to affect
     * @param menu The
     * @param interfaceManager The interface manager to use
     */
    void affectPlayer(@Nonnull FormPlayer player, @Nonnull JavaMenu menu, @Nonnull InterfaceManager interfaceManager);
}
