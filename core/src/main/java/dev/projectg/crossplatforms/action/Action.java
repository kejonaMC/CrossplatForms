package dev.projectg.crossplatforms.action;

import dev.projectg.crossplatforms.config.serializer.KeyedType;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

@ConfigSerializable
public interface Action extends KeyedType {

    /**
     * Affects a player
     * @param player The player to affect
     * @param interfaceManager The interface manager to use
     * @param bedrockHandler The bedrock handler to use
     */
    default void affectPlayer(@Nonnull FormPlayer player, @Nonnull InterfaceManager interfaceManager, @Nonnull BedrockHandler bedrockHandler) {
        affectPlayer(player, Collections.emptyMap(), interfaceManager, bedrockHandler);
    }

    /**
     * Affects a player
     * @param player The player to affect
     * @param additionalPlaceholders Additional placeholders to resolve
     * @param interfaceManager The interface manager to use
     * @param bedrockHandler The bedrock handler to use
     */
    void affectPlayer(@Nonnull FormPlayer player,
                      @Nonnull Map<String, String> additionalPlaceholders,
                      @Nonnull InterfaceManager interfaceManager,
                      @Nonnull BedrockHandler bedrockHandler);

    // Static methods for batching multiple actions together:

    static void affectPlayer(@Nonnull FormPlayer player,
                             @Nonnull Iterable<Action> actions,
                             @Nonnull Map<String, String> additionalPlaceholders,
                             @Nonnull InterfaceManager interfaceManager,
                             @Nonnull BedrockHandler bedrockHandler) {

        actions.forEach(a -> a.affectPlayer(player, additionalPlaceholders, interfaceManager, bedrockHandler));
    }

    static void affectPlayer(@Nonnull FormPlayer player,
                             @Nonnull Iterable<Action> actions,
                             @Nonnull InterfaceManager interfaceManager,
                             @Nonnull BedrockHandler bedrockHandler) {

        actions.forEach(a -> a.affectPlayer(player, Collections.emptyMap(), interfaceManager, bedrockHandler));
    }
}
