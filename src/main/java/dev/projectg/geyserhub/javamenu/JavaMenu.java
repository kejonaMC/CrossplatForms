package dev.projectg.geyserhub.javamenu;

import dev.projectg.geyserhub.GeyserHubMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JavaMenu {
    public static void openMenu(Player player){

    Inventory gui = Bukkit.createInventory(player, GeyserHubMain.getInstance().getConfig().getInt("JavaMenuSlot"), ChatColor.DARK_AQUA + "Server Selector");
    ConfigurationSection Selector = GeyserHubMain.getInstance().getConfig().getConfigurationSection("JavaSelector");
    assert Selector != null;
    for (String key : Selector.getKeys(false)) {

        String name = GeyserHubMain.getInstance().getConfig().getString("JavaSelector." + key + ".Name");
        Material material = Material.matchMaterial(Objects.requireNonNull(GeyserHubMain.getInstance().getConfig().getString("JavaSelector." + key + ".Material")));
        int slot = GeyserHubMain.getInstance().getConfig().getInt("JavaSelector." + key + ".Slot");
        assert material != null;
        ItemStack selectorStack = new ItemStack(material);
        ItemMeta NameM = selectorStack.getItemMeta();
        assert NameM != null;
        NameM.setDisplayName(name);

        // get server name from item
        assert GeyserHubMain.getInstance() != null;
        String bungeeName = GeyserHubMain.getInstance().getConfig().getString("JavaSelector." + key + ".Server");
        assert bungeeName != null;
        NameM.getPersistentDataContainer().set(new NamespacedKey(GeyserHubMain.getInstance(), "bungeeName"), PersistentDataType.STRING, bungeeName);

        // add the lore
        List<String> lore = new ArrayList<>(GeyserHubMain.getInstance().getConfig().getStringList("JavaSelector." + key + ".Lore"));
        NameM.setLore(lore);
        selectorStack.setItemMeta(NameM);
        gui.setItem(slot, selectorStack);
        player.openInventory(gui);
                    }
    }
}
