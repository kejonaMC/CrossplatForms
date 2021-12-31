package dev.projectg.geyserhub.form.java;

import dev.projectg.geyserhub.form.button.OutcomeButton;
import org.bukkit.Material;

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

        this.rightClickButton = button.getOutcomeButton(true);
        this.leftClickButton = button.getOutcomeButton(false);
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
     * Get the {@link OutcomeButton} for when the player clicks on this ItemButton.
     * Warning: the text of the OutcomeButton is ignored.
     * @param rightClick True to get the right side OutcomeButton, or false to get the left side.
     * @return the OutcomeButton
     */
    public @Nonnull OutcomeButton getOutcomeButton(boolean rightClick) {
        if (rightClick) {
            return rightClickButton;
        } else {
            return leftClickButton;
        }
    }
}
