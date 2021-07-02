package dev.projectg.geyserhub.module.menu;


import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.config.ConfigId;
import dev.projectg.geyserhub.reloadable.Reloadable;
import dev.projectg.geyserhub.reloadable.ReloadableRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AccessItem implements Reloadable {

    private static final String NON_LEGACY_MATERIAL_VERSIONS = "(1\\.14\\S*)|(1\\.15\\S*|1\\.16\\S*|1\\.17\\S*|1\\.18\\S*)";

    private static ItemStack ACCESS_ITEM;
    static {
        new AccessItem();
    }

    public static ItemStack getItem() {
        return ACCESS_ITEM;
    }

    public AccessItem() {
        reload();
        ReloadableRegistry.registerReloadable(this);
    }

    @Override
    public boolean reload() {
        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.SELECTOR);

        // Get the material
        Material material;
        if (config.contains("Selector-Item.Material", true)) {
            String materialName = config.getString("Selector-Item.Material");
            Objects.requireNonNull(materialName);
            material = Material.getMaterial(materialName);
            if (material == null) {
                SelectorLogger.getLogger().warn("Failed to find a Material for \"" + materialName + "\". Defaulting to COMPASS for the access item.");
                material = Material.COMPASS;
            } else {
                // Hacky way to avoid using enum values that don't exist on older versions if we are on older versions
                if (Bukkit.getServer().getVersion().matches(NON_LEGACY_MATERIAL_VERSIONS)) {
                    if (material == Material.AIR || material == Material.CAVE_AIR || material == Material.VOID_AIR) {
                        SelectorLogger.getLogger().warn("\"Selector-Item.Material\" cannot be AIR! Choose a different material. Defaulting to COMPASS for the access item.");
                    }
                } else {
                    if (material == Material.AIR) {
                        SelectorLogger.getLogger().warn("\"Selector-Item.Material\" cannot be AIR! Choose a different material. Defaulting to COMPASS for the access item.");
                    }
                }
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
        meta.setDisplayName(name);

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

        return true;
    }
}
