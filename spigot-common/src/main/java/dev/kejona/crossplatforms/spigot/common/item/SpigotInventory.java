package dev.kejona.crossplatforms.spigot.common.item;

import dev.kejona.crossplatforms.item.Inventory;
import dev.kejona.crossplatforms.item.InventoryLayout;
import dev.kejona.crossplatforms.item.Item;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class SpigotInventory implements Inventory {

    private final org.bukkit.inventory.Inventory handle;

    public SpigotInventory(String title, int chestSize) {
        handle = Bukkit.createInventory(null, chestSize, title);
    }

    public SpigotInventory(String title, InventoryLayout layout) {
        handle = Bukkit.createInventory(null, convertType(layout), title);
    }

    @Override
    public Object handle() {
        return handle;
    }

    @Override
    public void setSlot(int index, Item item) {
        handle.setItem(index, item.castedHandle());
    }

    public static InventoryType convertType(InventoryLayout layout) {
        switch (layout) {
            case CHEST:
                return InventoryType.CHEST;
            case HOPPER:
                return InventoryType.HOPPER;
            case DISPENSER:
                return InventoryType.DISPENSER;
        }

        throw new AssertionError("Unhandled InventoryLayout: " + layout.name());
    }
}
