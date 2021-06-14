package dev.projectg.geyserhub.module.menu;


import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AccessItem {

    private static final ItemStack ACCESS_ITEM;
    static {
        FileConfiguration config = GeyserHubMain.getInstance().getConfig();

        // Get the material
        Material material;
        if (config.contains("Selector-Item.Material", true)) {
            String materialName = config.getString("Selector-Item.Material");
            Objects.requireNonNull(materialName);
            material = Material.getMaterial(materialName, false);
            if (material == null) {
                material = Material.getMaterial(materialName, true);
                if (material == null) {
                    SelectorLogger.getLogger().warn("Failed to find a Material for \"" + materialName + "\". Defaulting to COMPASS for the access item.");
                    material = Material.COMPASS;
                }
            }
            if (material == Material.AIR || material == Material.CAVE_AIR || material == Material.VOID_AIR) {
                SelectorLogger.getLogger().warn("\"Selector-Item.Material\" cannot be AIR! Choose a different material. Defaulting to COMPASS for the access item.");
            }
        } else {
            SelectorLogger.getLogger().warn("Failed to find \"Selector-Item.Material\" in the config! Defaulting to COMPASS for the access item.");
            material = Material.COMPASS;
        }

        // Create a new item with the material, get the meta
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta);

        // Set the display name in the meta
        String name;
        if (config.contains("Selector-Item.Name", true)) {
            name = config.getString("Selector-Item.Name");
            Objects.requireNonNull(name);
        } else {
            SelectorLogger.getLogger().warn("\"Selector-Item.Name\" in the config does not exist. Defaulting to \"&6Server Selector\" for the access item name.");
            name = "&6Server Selector";
        }
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        // Set the lore in the meta
        List<String> lore;
        if (config.contains("Selector-Item.Lore") && config.isList("Selector-Item.Lore")) {
            lore = config.getStringList("Selector-Item.Lore");
        } else {
            lore = Collections.emptyList();
        }
        meta.setLore(lore);


        // Set the meta and set the field
        item.setItemMeta(meta);
        ACCESS_ITEM = item;
        SelectorLogger.getLogger().debug("Created and set the access item from the configuration.");
    }

    public static ItemStack getItem() {
        return ACCESS_ITEM;
    }
}
