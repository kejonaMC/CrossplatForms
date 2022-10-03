package dev.kejona.crossplatforms.action;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.serialize.ValuedType;

import javax.annotation.Nonnull;

public interface Action<T> extends ValuedType {

    /**
     * Affects a player
     * @param player The player to affect
     */
    void affectPlayer(@Nonnull FormPlayer player, @Nonnull Resolver resolver, @Nonnull T executor);

    // Static methods for batching multiple actions together:

    static <T> void affectPlayer(@Nonnull FormPlayer player,
                             @Nonnull Iterable<Action<? super T>> actions,
                             @Nonnull Resolver resolver,
                             @Nonnull T executor) {

        actions.forEach(a -> a.affectPlayer(player, resolver, executor));
    }

    /**
     * Native actions whose type will always be successfully inferred
     */
    static boolean typeInferrable(String type) {
        return type.equals("form")
                || type.equals("server")
                || type.equals("message") || type.equals("messages")
                || type.equals("commands");
    }
}
