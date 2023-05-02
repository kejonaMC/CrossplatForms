package dev.kejona.crossplatforms.inventory;

import dev.kejona.crossplatforms.handler.FormPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface InventoryFactory {

    InventoryHandle chest(String title, int chestSize);
    InventoryHandle inventory(String title, InventoryLayout layout);

    ItemHandle item(@Nullable String material, @Nullable String displayName, @Nonnull List<String> lore, @Nullable Integer customModelData);

    ItemHandle skullItem(FormPlayer profile, @Nullable String displayName, List<String> lore);
    ItemHandle skullItem(SkullProfile profile, @Nullable String displayName, List<String> lore);
}
