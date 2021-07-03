package dev.projectg.geyserhub.module.menu;

import dev.projectg.geyserhub.module.menu.bedrock.BedrockFormRegistry;
import dev.projectg.geyserhub.module.menu.java.JavaMenuRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CommonMenuListeners implements Listener {

    private final AccessItemRegistry accessItemRegistry;
    private final BedrockFormRegistry bedrockFormRegistry;
    private final JavaMenuRegistry javaMenuRegistry;

    public CommonMenuListeners(AccessItemRegistry accessItemRegistry, BedrockFormRegistry bedrockFormRegistry, JavaMenuRegistry javaMenuRegistry) {
        this.accessItemRegistry = accessItemRegistry;
        this.bedrockFormRegistry = bedrockFormRegistry;
        this.javaMenuRegistry = javaMenuRegistry;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) { // open the menu through the access item
        if (!accessItemRegistry.isEnabled()) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();
            if (item != null) {
                AccessItem accessItem = accessItemRegistry.getAccessItem(item);
                if (accessItem != null) {
                    event.setCancelled(true); // todo: what happens if we don't cancel this? does the chest open before or after ours?
                    String formName = accessItem.formName;
                    MenuUtils.sendForm(player, bedrockFormRegistry, javaMenuRegistry, formName);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) { // keep the access item in place (depending on config)
        if (!accessItemRegistry.isEnabled()) {
            return;
        }

        // todo: don't allow duplication for creative players
        ItemStack item = event.getCurrentItem();
        if (item != null) {
            AccessItem accessItem = accessItemRegistry.getAccessItem(item);
            if (accessItem != null) {
                event.setCancelled(!accessItem.allowMove);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) { // don't let the access item be dropped, destroy it if it is (depending on config)
        if (!accessItemRegistry.isEnabled()) {
            return;
        }

        ItemStack item = event.getItemDrop().getItemStack();
        AccessItem accessItem = accessItemRegistry.getAccessItem(item);
        if (accessItem != null) {
            if (!accessItem.allowDrop) {
                event.setCancelled(true);
            } else if (accessItem.destroyDropped) {
                event.getItemDrop().remove();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { // give the access item when the player joins
        if (!accessItemRegistry.isEnabled()) {
            return;
        }

        Player player = event.getPlayer();

        // Remove any access items that are already in the inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                continue;
            }
            if (AccessItemRegistry.getAccessItemId(item) != null) {
                player.getInventory().remove(item);
            }
        }

        for (AccessItem accessItem : accessItemRegistry.getAccessItems().values()) {
            if (accessItem.onJoin) {
                ItemStack accessItemStack = accessItem.getItemStack(player); // todo update placeholders

                int desiredSlot = accessItem.slot;
                ItemStack oldItem = player.getInventory().getItem(desiredSlot);
                boolean success = false;
                if (oldItem == null || oldItem.getType() == Material.AIR) {
                    player.getInventory().setItem(desiredSlot, accessItemStack);
                    success = true;
                } else {
                    for (int i = 0; i < 10; i++) {
                        if (player.getInventory().getItem(i) == null || Objects.requireNonNull(player.getInventory().getItem(i)).getType() == Material.AIR) {
                            player.getInventory().setItem(i, oldItem);
                            player.getInventory().setItem(desiredSlot, accessItemStack);
                            success = true;
                            break;
                        }
                    }
                    // If the player doesn't have the space in their hotbar then they don't get it
                }
                if (success) {
                    event.getPlayer().getInventory().setHeldItemSlot(accessItem.slot);
                }
            }
        }
    }
}
