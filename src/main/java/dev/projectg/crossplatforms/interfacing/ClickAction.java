package dev.projectg.crossplatforms.interfacing;

import org.bukkit.entity.Player;
import org.geysermc.geyser.inventory.click.Click;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;

public interface ClickAction {

    void affectPlayer(@Nonnull Player player);

    void affectPlayer(@Nonnull Player player, @Nonnull Map<String, String> additionalPlaceholders);
}
