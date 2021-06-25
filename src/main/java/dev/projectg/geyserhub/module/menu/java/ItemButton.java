package dev.projectg.geyserhub.module.menu.java;

import dev.projectg.geyserhub.module.menu.button.OutcomeButton;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ItemButton {

    @Nonnull private String displayName;
    @Nonnull private Material material;
    @Nonnull private List<String> lore = Collections.emptyList();

    @Nonnull private final OutcomeButton rightClickButton;
    @Nonnull private final OutcomeButton leftClickButton;

    /**
     * Main constructor.
     * @param text the display name of the item button
     * @param material the material of the item button
     */
    public ItemButton(@Nonnull String text, @Nonnull Material material) {
        this.displayName = Objects.requireNonNull(text);
        this.material = Objects.requireNonNull(material);

        rightClickButton = new OutcomeButton(text);
        leftClickButton = new OutcomeButton(text);
    }

    /**
     * Copy constructor.
     * @param button The button to make a copy of
     */
    public ItemButton(@Nonnull ItemButton button) {
        this.displayName = button.getDisplayName();
        this.material = button.getMaterial();
        this.lore = button.getLore();

        this.rightClickButton = button.getRightClickButton();
        this.leftClickButton = button.getLeftClickButton();
    }

    public @Nonnull String getDisplayName() {
        return this.displayName;
    }
    public @Nonnull Material getMaterial() {
        return this.material;
    }
    public @Nonnull List<String> getLore() {
        return new ArrayList<>(lore); // lists are mutable, make a new list instance
    }

    public void setDisplayName(@Nonnull String displayName) {
        Objects.requireNonNull(displayName);
        this.displayName = displayName;
    }
    public void setMaterial(@Nonnull Material material) {
        Objects.requireNonNull(material);
        this.material = material;
    }
    public void setLore(@Nonnull List<String> lore) {
        Objects.requireNonNull(lore);
        this.lore = lore;
    }

    /**
     * Get the OutcomeButton for when the player right clicks on this ItemButton.
     * Warning: the text of the OutcomeButton is ignored.
     * @return the OutcomeButton
     */
    public @NotNull OutcomeButton getRightClickButton() {
        return rightClickButton;
    }

    /**
     * Get the OutcomeButton for when the player left clicks on this ItemButton.
     * Warning: the text of the OutcomeButton is ignored.
     * @return the OutcomeButton
     */
    public @NotNull OutcomeButton getLeftClickButton() {
        return leftClickButton;
    }
}
