package dev.kejona.crossplatforms.spigot.item;

import dev.kejona.crossplatforms.item.Item;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class SpigotItem implements Item {

    private final ItemStack handle;

    @Override
    public Object handle() {
        return handle;
    }
}
