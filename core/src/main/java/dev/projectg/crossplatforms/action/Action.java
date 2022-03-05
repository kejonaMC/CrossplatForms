package dev.projectg.crossplatforms.action;

import dev.projectg.crossplatforms.config.IdentifiableType;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

@ConfigSerializable
public interface Action extends IdentifiableType {

    /**
     * Affects a Java Edition player.
     * @param player The JE player to affect
     * @param interfaceManager The interface manager to use
     */
    default void affectPlayer(@Nonnull FormPlayer player, @Nonnull InterfaceManager interfaceManager) {
        affectPlayer(player, interfaceManager, BedrockHandler.empty());
    }

    /**
     * Affects a Java Edition player.
     * @param player The JE player to affect
     * @param interfaceManager The interface manager to use
     * @param additionalPlaceholders Additional placeholders to resolve
     */
    default void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders, @Nonnull InterfaceManager interfaceManager) {
        affectPlayer(player, additionalPlaceholders, interfaceManager, BedrockHandler.empty());
    }

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
    void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders, @Nonnull InterfaceManager interfaceManager, @Nonnull BedrockHandler bedrockHandler);
}
