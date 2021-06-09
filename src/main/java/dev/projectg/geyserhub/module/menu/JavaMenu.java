package dev.projectg.geyserhub.module.menu;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.module.Placeholders;
import dev.projectg.geyserhub.utils.bstats.SelectorLogger;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class JavaMenu extends Placeholders {

    public static boolean isEnabled() {
        return GeyserHubMain.getInstance().getConfig().getBoolean("Java-Selector.Enabled", true);
    }

    public static void openMenu(@Nonnull Player player, @Nonnull FileConfiguration config) {
        SelectorLogger logger = SelectorLogger.getLogger();

        // Get the java selector section
        if (!config.contains("Java-Selector") || !config.isConfigurationSection("Java-Selector") || !config.contains("Java-Selector.Servers") || !config.isConfigurationSection("Java-Selector.Servers")) {
            logger.warn("Failed to create a java selector because the configuration is malformed!");
            return;
        }
        ConfigurationSection javaSection = config.getConfigurationSection("Java-Selector");
        assert javaSection != null;
        ConfigurationSection serverSection = javaSection.getConfigurationSection("Servers");
        assert serverSection != null;

        Inventory selectorGUI = Bukkit.createInventory(player, javaSection.getInt("Size"), ChatColor.DARK_AQUA + javaSection.getString("Title"));

        // Loop through every server entry
        for (String serverName : serverSection.getKeys(false)) {
            String failMessage = "Server entry with name \"" + serverName + "\" was not added to the java selector";

            if (!serverSection.isConfigurationSection(serverName)) {
                logger.warn(failMessage + " because it is not a configuration section!");
                continue;
            }
            ConfigurationSection serverInfo = serverSection.getConfigurationSection(serverName);
            assert serverInfo != null;

            if (serverInfo.contains("Display-Name", true) && serverInfo.isString("Display-Name") && serverInfo.contains("Material", true) && serverInfo.isString("Material") && serverInfo.contains("Slot", true) && serverInfo.isInt("Slot")) {

                // Get all the required info
                String displayName = serverInfo.getString("Display-Name");
                assert displayName != null;
                String materialName = serverInfo.getString("Material");
                Material material = Material.matchMaterial(Objects.requireNonNull(serverInfo.getString("Material")));
                if (material == null || material.isAir()) {
                    logger.warn(failMessage + " because \"" + materialName + "\" is not a valid material!");
                    continue;
                }
                int slot = serverInfo.getInt("Slot");

                // Construct the item
                ItemStack serverStack = new ItemStack(material);
                ItemMeta itemMeta = serverStack.getItemMeta();
                // It will only be null if the itemstack is air, which we already filter against.
                assert itemMeta != null;
                itemMeta.setDisplayName(displayName);
                itemMeta.getPersistentDataContainer().set(new NamespacedKey(GeyserHubMain.getInstance(), "bungeeName"), PersistentDataType.STRING, serverName);
                if (serverInfo.contains("Lore", false) && serverInfo.isList("Lore")) {
                    List<String> lore = serverInfo.getStringList("Lore");
                    List<String> withPlaceholders = PlaceholderAPI.setPlaceholders(player, lore);
                    itemMeta.setLore(withPlaceholders);
                } else {
                    logger.debug("Server entry with name \"" + serverName + "\" does not have a valid lore list");
                }
                serverStack.setItemMeta(itemMeta);

                selectorGUI.setItem(slot, serverStack);
            } else {
                logger.warn(failMessage + " because it does not contain a valid Display-Name, Material, or Slot value!");
            }
        }
        player.openInventory(selectorGUI);
    }
}
