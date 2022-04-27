package dev.projectg.crossplatforms.spigot;

import dev.projectg.crossplatforms.interfacing.java.ItemButton;
import dev.projectg.crossplatforms.spigot.common.SpigotInterfacerBase;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class LegacySpigotInterfacer extends SpigotInterfacerBase {

    private static final String BUTTON_IDENTIFIER = ItemButton.STATIC_IDENTIFIER;

    @Override
    public void setMenuName(@Nonnull ItemStack stack, @Nonnull String identifier) {
        NbtUtils.setCustomString(stack, BUTTON_IDENTIFIER, identifier);
    }

    @Nullable
    @Override
    public String getMenuName(@Nonnull ItemStack stack) {
        return NbtUtils.getString(stack, BUTTON_IDENTIFIER);
    }
}
