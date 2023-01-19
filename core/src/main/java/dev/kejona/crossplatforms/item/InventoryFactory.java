package dev.kejona.crossplatforms.item;

import dev.kejona.crossplatforms.handler.FormPlayer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.OptionalInt;

public interface InventoryFactory {

    Inventory chest(String title, int chestSize);
    Inventory inventory(String title, InventoryLayout layout);

    Item item(String displayName, String material, List<String> lore, OptionalInt customModelData);

    Item skullItem(FormPlayer owner, @Nullable String displayName, List<String> lore);
}
