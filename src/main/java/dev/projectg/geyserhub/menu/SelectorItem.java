package dev.projectg.geyserhub.menu;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SelectorItem {

    private static final ItemStack SELECTOR_ITEM;
    static {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        assert compassMeta != null;
        compassMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Server Selector"));
        compass.setItemMeta(compassMeta);
        SELECTOR_ITEM = compass;
    }

    public static ItemStack getItem() {
        return SELECTOR_ITEM;
    }
}
