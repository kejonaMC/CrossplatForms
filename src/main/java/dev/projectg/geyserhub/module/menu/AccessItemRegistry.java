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
        if (!selectorConfig.contains("Items") || !selectorConfig.isConfigurationSection("Items")) {
            logger.warn("Not creating any access items because Selector-Item in selector.yml does not list any items!");
            return;
        }
        ConfigurationSection configItems = Objects.requireNonNull(selectorConfig.getConfigurationSection("Items"));

        for (String itemId : configItems.getKeys(false)) {
            if (!configItems.isConfigurationSection(itemId)) {
                continue;
            }
            ConfigurationSection itemSection = configItems.getConfigurationSection(itemId);
            Objects.requireNonNull(itemSection);

            // Get the material
            Material material;
            if (itemSection.contains("Material", true)) {
                String materialName = itemSection.getString("Material");
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
            if (itemSection.contains("Name", true)) {
                name = itemSection.getString("Selector-Item.Name");
                Objects.requireNonNull(name);
            } else {
                SelectorLogger.getLogger().warn("Failed to find Selector-Item. " + itemId + ".Name in the config! Defaulting to '§6Server Selector'.");
                name = "§6Server Selector";
            }
            meta.setDisplayName(name);

            // Set the lore in the meta
            List<String> lore;
            if (itemSection.contains("Lore", true) && itemSection.isList("Lore")) {
                lore = itemSection.getStringList("Selector-Item.Lore");
            } else {
                lore = Collections.emptyList();
            }
            meta.setLore(lore);

            // Set the meta and set the field
            item.setItemMeta(meta);

            if (!itemSection.contains("Form") || !itemSection.isString("Form")) {
                logger.warn("Access item: " + itemId + " does not contain a form! Not registering the item.");
                continue;
            }
            String formName = Objects.requireNonNull(itemSection.getString("Form"));

            if (itemSection.contains("Slot", true) && itemSection.isInt("Slot")) {
                if ( !itemSection.contains("Join") || !itemSection.contains("Allow-Drop") || !itemSection.contains("Destroy-Dropped") || !itemSection.contains("Allow-Move")
                || !itemSection.isBoolean("Join") || !itemSection.isBoolean("Allow-Drop") || !itemSection.isBoolean("Destroy-Dropped") || !itemSection.isBoolean("Allow-Move")) {
                    logger.warn("Failed to create access item " + itemId + " because it is missing config values!");
                    continue;
                }
                int slot = Math.abs(configItems.getInt("Slot"));
                items.put(itemId, new AccessItem(itemId, slot, name, material, lore,
                        itemSection.getBoolean("Join"), itemSection.getBoolean("Allow-Drop"),
                        itemSection.getBoolean("Destroy-Dropped"), itemSection.getBoolean("Allow-Move"),
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
     * Attempt to retrieve the Access Item ID that an ItemStack points to
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
