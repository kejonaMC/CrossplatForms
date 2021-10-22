package dev.projectg.geyserhub.module.menu.java;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.config.ConfigId;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;

public class JavaMenuListeners implements Listener {

    private final JavaMenuRegistry javaMenuRegistry;

    public JavaMenuListeners(@Nonnull JavaMenuRegistry javaMenuRegistry) {
        this.javaMenuRegistry = Objects.requireNonNull(javaMenuRegistry);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // This is used for processing inventory clicks WITHIN the java menu GUI

        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.SELECTOR);
        if (!config.getBoolean("Java-Selector.Enable")) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        SelectorLogger logger = SelectorLogger.getLogger();

        ItemStack item = event.getCurrentItem();
        if (item != null) {
            JavaMenu menu = javaMenuRegistry.getMenu(item);
            if (menu == null) {
                logger.warn("Failed to find any Java menu for the itemstack of'" + (item.hasItemMeta() ? Objects.requireNonNull(item.getItemMeta()).getDisplayName() : item.toString()) + "' in order to process inventory click by player: " + player.getName());
            } else {
                event.setCancelled(true);
                menu.process(event.getSlot(), event.isRightClick(), player);
            }
        }
    }
}