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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public class InventoryManager implements Listener {

    private final AccessItemRegistry accessItemRegistry;
    private final BedrockFormRegistry bedrockFormRegistry;
    private final JavaMenuRegistry javaMenuRegistry;

    public InventoryManager(AccessItemRegistry accessItemRegistry, BedrockFormRegistry bedrockFormRegistry, JavaMenuRegistry javaMenuRegistry) {
        this.accessItemRegistry = accessItemRegistry;
        this.bedrockFormRegistry = bedrockFormRegistry;
        this.javaMenuRegistry = javaMenuRegistry;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) { // opening menus through access items
        if (!accessItemRegistry.isEnabled()) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null) {
                AccessItem accessItem = accessItemRegistry.getAccessItem(item);
                if (accessItem != null) {
                    event.setCancelled(true); // todo: what happens if we don't cancel this? does the chest open before or after ours?

                    Player player = event.getPlayer();
                    String formName = accessItem.formName;
                    MenuUtils.sendForm(player, bedrockFormRegistry, javaMenuRegistry, formName);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) { // keep the access items in place
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
    public void onPlayerDropItem(PlayerDropItemEvent event) { // don't let the access item be dropped, destroy it if it is
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
        giveAccessItems(event.getPlayer(), accessItemRegistry, accessItem -> accessItem.onJoin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) { // give the access item when the player respawns
        giveAccessItems(event.getPlayer(), accessItemRegistry, accessItem -> accessItem.onRespawn);
    }

    /**
     * Remove all the access items from a player's inventory
     * @param player The player whose inventory to remove from
     */
    private static void removeAccessItems(Player player) {
        // Remove any access items that are already in the inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                if (AccessItemRegistry.getAccessItemId(item) != null) {
                    // Even if this specific item/access item is no longer registered
                    // The fact it has the ID inside of it means it once was or still is
                    player.getInventory().remove(item);
                }
            }
        }
    }

    /**
     * Clears all access items a player has, gives them all access items from a given registry
     * @param player The player to give the access items to
     * @param registry The registry to get access items from
     * @param addItem Should test true if the access item should be added
     */
    private static void giveAccessItems(Player player, AccessItemRegistry registry, Predicate<AccessItem> addItem) {
        if (!registry.isEnabled()) {
            return;
        }

        removeAccessItems(player);

        boolean holdItem = true; // True if the next access item should have be set as the held slot
        for (AccessItem accessItem : registry.getAccessItems().values()) {
            if (addItem.test(accessItem)) {
                if (giveAccessItem(player, accessItem, holdItem)) {
                    // Only set the held item once
                    holdItem = false;
                }
            }
        }
    }

    /**
     * Gives a player an access item
     * @param player The player to give the access item to
     * @param accessItem The access item to give
     * @param setHeldSlot True if the player's selected item should be forced to the access item
     * @return True if the access item was successfully given
     */
    private static boolean giveAccessItem(Player player, AccessItem accessItem, boolean setHeldSlot) {
        ItemStack accessItemStack = accessItem.getItemStack(player); // todo update placeholders after the fact. but when?

        int desiredSlot = accessItem.slot;
        ItemStack oldItem = player.getInventory().getItem(desiredSlot);
        boolean success = false;
        if (oldItem == null || oldItem.getType() == Material.AIR) {
            // put the item in the desired place
            player.getInventory().setItem(desiredSlot, accessItemStack);
            success = true;
        } else {
            for (int i = 0; i < 9; i++) {
                // Try and find an empty slot

                ItemStack otherItem = player.getInventory().getItem(i);
                if (otherItem == null || otherItem.getType() == Material.AIR) {
                    // slot is empty, move the item that is blocking us to it
                    player.getInventory().setItem(i, oldItem);
                    // put the access item in the slot that is no longer blocked
                    player.getInventory().setItem(desiredSlot, accessItemStack);
                    success = true;
                    break;
                }
            }
            // If the player doesn't have the space in their hotbar then they don't get it
        }

        if (success) {
            if (setHeldSlot) {
                // Set the held item to the slot of the access item
                player.getInventory().setHeldItemSlot(accessItem.slot);
            }
            return true;
        } else {
            return false;
        }
    }
}
