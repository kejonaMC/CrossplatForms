package dev.projectg.geyserhub.module.menu;


import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.config.ConfigId;
import dev.projectg.geyserhub.reloadable.Reloadable;
import dev.projectg.geyserhub.reloadable.ReloadableRegistry;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AccessItemRegistry implements Reloadable {

    private final GeyserHubMain geyserHub;
    private final Map<String, AccessItem> items = new HashMap<>();

    public AccessItemRegistry(GeyserHubMain geyserHub) {
        this.geyserHub = geyserHub;
        ReloadableRegistry.registerReloadable(this);
    }

    private void fetchItems() {
        SelectorLogger logger = SelectorLogger.getLogger();

        ConfigurationSection configItems = geyserHub.getConfigManager().getFileConfiguration(ConfigId.SELECTOR);
        for (String itemId : configItems.getKeys(false)) {
            if (!configItems.isConfigurationSection(itemId)) {
                continue;
            }
            ConfigurationSection itemSection = configItems.getConfigurationSection(itemId);
            Objects.requireNonNull(itemSection);

            // Get the material
            Material material;
            if (itemSection.contains("Selector-Item.Material", true)) {
                String materialName = itemSection.getString("Selector-Item.Material");
                Objects.requireNonNull(materialName);
                material = Material.getMaterial(materialName);
                if (material == null) {
                    SelectorLogger.getLogger().warn("Failed to find a Material for '" + materialName + "' for access item: " + itemId + ". Defaulting to COMPASS for the access item.");
                    material = Material.COMPASS;
                }
            } else {
                SelectorLogger.getLogger().warn("Failed to find Selector-Item. " + itemId + ".Material in the config! Defaulting to COMPASS.");
                material = Material.COMPASS;
            }

            // Create a new item with the material, get the meta
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                logger.warn("Failed to create access item " + itemId + " with Material " + material + " because the ItemMeta returned null.");
                continue;
            }

            // Set the display name in the meta
            String name;
            if (itemSection.contains("Selector-Item.Name", true)) {
                name = itemSection.getString("Selector-Item.Name");
                Objects.requireNonNull(name);
            } else {
                SelectorLogger.getLogger().warn("Failed to find Selector-Item. " + itemId + ".Name in the config! Defaulting to '§6Server Selector'.");
                name = "§6Server Selector";
            }
            meta.setDisplayName(name);

            // Set the lore in the meta
            List<String> lore;
            if (itemSection.contains("Selector-Item.Lore", true) && itemSection.isList("Selector-Item.Lore")) {
                lore = itemSection.getStringList("Selector-Item.Lore");
            } else {
                lore = Collections.emptyList();
            }
            meta.setLore(lore);

            // Set the meta and set the field
            item.setItemMeta(meta);

            if (itemSection.contains("Slot", true) && itemSection.isInt("Slot")) {
                if ( !itemSection.contains("Join") || !itemSection.contains("Allow-Drop") || !itemSection.contains("Destroy-Dropped") || !itemSection.contains("Allow-Move")
                || !itemSection.isBoolean("Join") || !itemSection.isBoolean("Allow-Drop") || !itemSection.isBoolean("Destroy-Dropped") || !itemSection.isBoolean("Allow-Move")) {
                    logger.warn("Failed to create access item " + itemId + " because it is missing config values!");
                    continue;
                }
                int slot = Math.abs(configItems.getInt("Slot"));
                items.put(itemId, new AccessItem(itemId, slot, name, material, lore,
                        itemSection.getBoolean("Join"),
                        itemSection.getBoolean("Allow-Drop"),
                        itemSection.getBoolean("Destroy-Dropped"),
                        itemSection.getBoolean("Allow-Move")));
            } else {
                logger.warn("Failed to create access item " + itemId + " because a Slot Integer value wasn't given.");
            }
        }
    }

    @Override
    public boolean reload() {
        items.clear();
        fetchItems();

        return true;
    }

    public HashMap<String, AccessItem> getAccessItems() {
        return new HashMap<>(items);
    }
}
