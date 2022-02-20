package dev.projectg.crossplatforms.interfacing;

import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.Player;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

public interface ClickAction {

    /**
     * Affects a Java Edition player.
     * @param player The JE player to affect
     * @param interfaceManager The interface manager to use
     */
    default void affectPlayer(@Nonnull Player player, @Nonnull InterfaceManager interfaceManager) {
        affectPlayer(player, interfaceManager, BedrockHandler.empty());
    }

    /**
     * Affects a Java Edition player.
     * @param player The JE player to affect
     * @param interfaceManager The interface manager to use
     * @param additionalPlaceholders Additional placeholders to resolve
     */
    default void affectPlayer(@Nonnull Player player,  @Nonnull Map<String, String> additionalPlaceholders, @Nonnull InterfaceManager interfaceManager) {
        affectPlayer(player, additionalPlaceholders, interfaceManager, BedrockHandler.empty());
    }

    /**
     * Affects a player
     * @param player The player to affect
     * @param interfaceManager The interface manager to use
     * @param bedrockHandler The bedrock handler to use
     */
    default void affectPlayer(@Nonnull Player player, @Nonnull InterfaceManager interfaceManager, @Nonnull BedrockHandler bedrockHandler) {
        affectPlayer(player, Collections.emptyMap(), interfaceManager, bedrockHandler);
    }

    /**
     * Affects a player
     * @param player The player to affect
     * @param additionalPlaceholders Additional placeholders to resolve
     * @param interfaceManager The interface manager to use
     * @param bedrockHandler The bedrock handler to use
     */
    void affectPlayer(@Nonnull Player player, @Nonnull Map<String, String> additionalPlaceholders, @Nonnull InterfaceManager interfaceManager, @Nonnull BedrockHandler bedrockHandler);
}
