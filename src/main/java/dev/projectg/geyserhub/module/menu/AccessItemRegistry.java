package dev.projectg.geyserhub.module.menu;


import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.config.ConfigId;
import dev.projectg.geyserhub.reloadable.Reloadable;
import dev.projectg.geyserhub.reloadable.ReloadableRegistry;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class AccessItemRegistry implements Reloadable {

    private boolean isEnabled = false;
    private final Map<String, AccessItem> items = new HashMap<>();

    public AccessItemRegistry() {
        load();
        ReloadableRegistry.registerReloadable(this);
    }

    private void load() {
        SelectorLogger logger = SelectorLogger.getLogger();

        FileConfiguration selectorConfig = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.SELECTOR);
        if (!selectorConfig.contains("Selector-Item") || !selectorConfig.isConfigurationSection("Selector-Item")) {
            logger.warn("Not creating any access items because selector.yml does not contain the Selector-Item config!");
            return;
        }
        ConfigurationSection accessSection = Objects.requireNonNull(selectorConfig.getConfigurationSection("Selector-Item"));
        if (accessSection.contains("Enable") && accessSection.isBoolean("Enable")) {
            if (!accessSection.getBoolean("Enable")) {
                return;
            }
        }
        if (!accessSection.contains("Items") || !accessSection.isConfigurationSection("Items")) {
            logger.warn("Not creating any access items because Selector-Item in selector.yml does not list any items!");
            return;
        }
        ConfigurationSection configItems = Objects.requireNonNull(accessSection.getConfigurationSection("Items"));

        for (String itemId : configItems.getKeys(false)) {
            if (!configItems.isConfigurationSection(itemId)) {
                continue;
            }
            ConfigurationSection itemEntry = configItems.getConfigurationSection(itemId);
            Objects.requireNonNull(itemEntry);

            // Get the material
            Material material;
            if (itemEntry.contains("Material", true)) {
                String materialName = itemEntry.getString("Material");
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
            if (itemEntry.contains("Name", true)) {
                name = itemEntry.getString("Name");
                Objects.requireNonNull(name);
            } else {
                SelectorLogger.getLogger().warn("Failed to find Selector-Item. " + itemId + ".Name in the config! Defaulting to '§6Server Selector'.");
                name = "§6Server Selector";
            }
            meta.setDisplayName(name);

            // Set the lore in the meta
            List<String> lore;
            if (itemEntry.contains("Lore", true) && itemEntry.isList("Lore")) {
                lore = itemEntry.getStringList("Lore");
            } else {
                lore = Collections.emptyList();
            }
            meta.setLore(lore);

            // Set the meta and set the field
            item.setItemMeta(meta);

            if (!itemEntry.contains("Form") || !itemEntry.isString("Form")) {
                logger.warn("Access item: " + itemId + " does not contain a form! Not registering the item.");
                continue;
            }
            String formName = Objects.requireNonNull(itemEntry.getString("Form"));

            if (itemEntry.contains("Slot", true) && itemEntry.isInt("Slot")) {
                if ( !itemEntry.contains("Join") || !itemEntry.contains("Allow-Drop") || !itemEntry.contains("Destroy-Dropped") || !itemEntry.contains("Allow-Move")
                || !itemEntry.isBoolean("Join") || !itemEntry.isBoolean("Allow-Drop") || !itemEntry.isBoolean("Destroy-Dropped") || !itemEntry.isBoolean("Allow-Move")) {
                    logger.warn("Failed to create access item " + itemId + " because it is missing config values!");
                    continue;
                }
                int slot = Math.abs(itemEntry.getInt("Slot"));
                items.put(itemId, new AccessItem(itemId, slot, name, material, lore,
                        itemEntry.getBoolean("Join"), itemEntry.getBoolean("Allow-Drop"),
                        itemEntry.getBoolean("Destroy-Dropped"), itemEntry.getBoolean("Allow-Move"),
                        formName));

                isEnabled = true;
            } else {
                logger.warn("Failed to create access item " + itemId + " because a Slot Integer value wasn't given.");
            }
        }
    }

    @Override
    public boolean reload() {
        items.clear();
        load();
        return true;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Get all the Access Items
     * @return A map whose keys are the identifiers of the access items, and the values are the access items
     */
    public HashMap<String, AccessItem> getAccessItems() {
        return new HashMap<>(items);
    }

    /**
     * Attempt to retrieve the Access Item that an ItemStack points to
     * @param itemStack The ItemStack to check. If it contains null ItemMeta, this will return null.
     * @return The Access Item if the ItemStack contained the identifier of the Access Item, and the Access Item exists. Will return null if their conditions are false.
     */
    @Nullable
    public AccessItem getAccessItem(@Nonnull ItemStack itemStack) {
        String identifier = getAccessItemId(itemStack);
        if (identifier == null) {
            return null;
        } else {
            return items.get(identifier);
        }
    }

    /**
     * Attempt to retrieve the Access Item ID that an ItemStack points to. The Access Item ID may or may not refer
     * to an actual AccessItem
     * @param itemStack The ItemStack to check
     * @return The AccessItem ID if the ItemStack contained the name, null if not.
     */
    @Nullable
    public static String getAccessItemId(@Nonnull ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        } else {
            return meta.getPersistentDataContainer().get(AccessItem.ACCESS_ITEM_KEY, AccessItem.ACCESS_ITEM_KEY_TYPE);
        }
    }
}
