package dev.kejona.crossplatforms.action;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.resolver.Resolver;

import javax.annotation.Nonnull;

public interface GenericAction extends Action<Object> {

    @Override
    default void affectPlayer(@Nonnull FormPlayer player, @Nonnull Resolver resolver, @Nonnull Object executor) {
        affectPlayer(player, resolver);
    }

    void affectPlayer(@Nonnull FormPlayer player, @Nonnull Resolver resolver);
}
