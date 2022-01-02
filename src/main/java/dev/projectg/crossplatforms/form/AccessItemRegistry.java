package dev.projectg.crossplatforms.form;


import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.reloadable.Reloadable;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;
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

    /**
     * Adds access items in {@link this#items} from {@link ConfigId#FORMS}.
     * Does not clear existing items.
     */
    private void load() {
        Logger logger = Logger.getLogger();

        FileConfiguration selectorConfig = CrossplatForms.getInstance().getConfigManager().getFileConfiguration(ConfigId.FORMS);
        if (!selectorConfig.contains("Access-Items") || !selectorConfig.isConfigurationSection("Access-Items")) {
            logger.warn("Not creating any access items because selector.yml does not contain the Access-Items config!");
            return;
        }
        ConfigurationSection accessSection = Objects.requireNonNull(selectorConfig.getConfigurationSection("Access-Items"));
        if (accessSection.contains("Enable") && accessSection.isBoolean("Enable")) {
            if (!accessSection.getBoolean("Enable")) {
                return;
            }
        }
        if (!accessSection.contains("Items") || !accessSection.isConfigurationSection("Items")) {
            logger.warn("Not creating any access items because Access-Items in selector.yml does not list any items!");
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
                    Logger.getLogger().warn("Failed to find a Material for '" + materialName + "' for access item: " + itemId + ". Defaulting to COMPASS for the access item.");
                    material = Material.COMPASS;
                }
            } else {
                Logger.getLogger().warn("Failed to find Access-Items. " + itemId + ".Material in the config! Defaulting to COMPASS.");
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
                Logger.getLogger().warn("Failed to find Access-Items. " + itemId + ".Name in the config! Defaulting to '§6Server Selector'.");
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
                int slot = Math.abs(itemEntry.getInt("Slot"));
                items.put(itemId, new AccessItem(itemId,
                        slot,
                        name,
                        material,
                        lore,
                        itemEntry.getBoolean("Join", true),
                        itemEntry.getBoolean("Respawn", true),
                        itemEntry.getBoolean("Allow-Drop", false),
                        itemEntry.getBoolean("Destroy-Dropped", true),
                        itemEntry.getBoolean("Allow-Move", false),
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
