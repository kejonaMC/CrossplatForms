package dev.kejona.crossplatforms.item;

import dev.kejona.crossplatforms.handler.FormPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface InventoryFactory {
    /*
    todo: This could be generified, along with config classes. this would allow for Configurate to directly serialize
    implementation-specific classes, such as material enums. Additionally, if Interface#openInventory was moved to this
    interface, then the usage of generics would mean that implementations would not have to cast Inventory/Item to get
    their implementation back
     */

    Inventory chest(String title, int chestSize);
    Inventory inventory(String title, InventoryLayout layout);

    Item item(@Nullable String material, @Nullable String displayName, @Nonnull List<String> lore, @Nullable Integer customModelData);

    Item skullItem(FormPlayer viewer, FormPlayer owner, @Nullable String displayName, List<String> lore);
    Item skullItem(FormPlayer viewer, SkullProfile owner, @Nullable String displayName, List<String> lore);
}
