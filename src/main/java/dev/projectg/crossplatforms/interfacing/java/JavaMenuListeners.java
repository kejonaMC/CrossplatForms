package dev.projectg.crossplatforms.interfacing.java;

import dev.projectg.crossplatforms.interfacing.IntefaceRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class JavaMenuListeners implements Listener {

    private final IntefaceRegistry intefaceRegistry;
    private final JavaMenuRegistry javaMenuRegistry;

    public JavaMenuListeners(IntefaceRegistry intefaceRegistry) {
        this.intefaceRegistry = intefaceRegistry;
        this.javaMenuRegistry = intefaceRegistry.getJavaRegistry();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // This is used for processing inventory clicks WITHIN the java menu GUI

        if (javaMenuRegistry.isEnabled()) {
            if (event.getWhoClicked() instanceof Player player) {
                ItemStack item = event.getCurrentItem();

                if (item != null) {
                    JavaMenu menu = javaMenuRegistry.getMenu(item);
                    if (menu != null) {
                        event.setCancelled(true);
                        menu.process(event.getSlot(), event.isRightClick(), player, intefaceRegistry);
                    }
                }
            }
        }
    }
}