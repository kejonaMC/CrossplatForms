package dev.projectg.geyserhub.module.menu.java;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class JavaMenuListeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        try {
            String bungeeName = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getPersistentDataContainer().get(new NamespacedKey(GeyserHubMain.getInstance(), "bungeeName"), PersistentDataType.STRING);
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Connect");
                assert bungeeName != null;
                out.writeUTF(bungeeName);
                player.sendPluginMessage(GeyserHubMain.getInstance(), "BungeeCord", b.toByteArray());
                player.sendMessage(ChatColor.DARK_AQUA + "Trying to send you to: " + ChatColor.GREEN + bungeeName);
            } catch (IOException er) {
                SelectorLogger.getLogger().severe("Failed to send a plugin message to Bungeecord!");
            }
            event.setCancelled(true);
        } catch (Exception ignored) {

        }
    }
}