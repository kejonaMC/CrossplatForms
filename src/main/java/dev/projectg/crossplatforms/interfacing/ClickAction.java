package dev.projectg.crossplatforms.interfacing;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;

public interface ClickAction {

    void affectPlayer(@Nonnull InterfaceManager interfaceManager, @Nonnull Player player);

    void affectPlayer(@Nonnull InterfaceManager interfaceManager, @Nonnull Player player, @Nonnull Map<String, String> additionalPlaceholders);
}
