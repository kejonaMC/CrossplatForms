package dev.projectg.crossplatforms.form;

import dev.projectg.crossplatforms.Platform;
import dev.projectg.crossplatforms.form.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.form.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.utils.InterfaceUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@AllArgsConstructor
public class InventoryManager implements Listener {

    private final AccessItemRegistry accessItemRegistry;
    private final BedrockFormRegistry bedrockFormRegistry;
    private final JavaMenuRegistry javaMenuRegistry;
    private final BedrockHandler bedrockHandler;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) { // opening menus through access items
        if (!accessItemRegistry.isEnabled()) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null) {
                AccessItem accessItem = accessItemRegistry.getItem(item);
                if (accessItem != null) {
                    event.setCancelled(true); // todo: what happens if we don't cancel this? does the chest open before or after ours?

                    Player player = event.getPlayer();
                    String formName = accessItem.getForm();
                    InterfaceUtils.sendInterface(player, bedrockFormRegistry, javaMenuRegistry, formName);
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
            AccessItem accessItem = accessItemRegistry.getItem(item);
            if (accessItem != null) {
                event.setCancelled(!event.getWhoClicked().hasPermission(accessItem.getMovePermission()));
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) { // restricting dropping
        if (!accessItemRegistry.isEnabled()) {
            return;
        }

        ItemStack item = event.getItemDrop().getItemStack();
        AccessItem accessItem = accessItemRegistry.getItem(item);
        if (accessItem != null) {
            Player player = event.getPlayer();
            if (player.hasPermission(accessItem.getDropPermission())) {
                if (!player.hasPermission(accessItem.getNoDestroyPermission())) {
                    event.getItemDrop().remove();
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { // give the access item when the player joins
        regive(event.getPlayer(), AccessItem::isOnJoin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) { // give the access item when the player respawns
        regive(event.getPlayer(), AccessItem::isOnRespawn);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        regive(event.getPlayer(), AccessItem::isOnWorldChange);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                AccessItem access = accessItemRegistry.getItem(item);
                if (access != null && !access.isPersist()) {
                    // Even if this specific item/access item is no longer registered
                    // The fact it has the ID inside of it means it once was or still is
                    player.getInventory().remove(item);
                }
            }
        }
    }

    private void regive(Player player, Predicate<AccessItem> give) {
        List<String> contained = new ArrayList<>(); // Access items the player already has and that will not be removed

        // Remove any access items that are now longer allowed
        for (ItemStack item : player.getInventory()) {
            if (item != null) {
                AccessItem access = accessItemRegistry.getItem(item);
                if (access != null) {
                    if (player.hasPermission(access.getMainPermission())) {
                        contained.add(access.getIdentifier());
                    } else {
                        player.getInventory().remove(item);
                    }
                }
            }
        }

        // Give any access items that should be given
        boolean changedHand = false; // If we have changed the item the player is holding
        for (AccessItem access : accessItemRegistry.getItems().values()) {
            if (give.test(access) && Platform.matches(player.getUniqueId(), access.getPlatform(), bedrockHandler)) {
                if (!contained.contains(access.getIdentifier())) {
                    if (accessItemRegistry.setHeldSlot() && !changedHand) {
                        giveAccessItem(player, access, true);
                        changedHand = true;
                    } else {
                        giveAccessItem(player, access, false);
                    }
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
        ItemStack accessItemStack = accessItem.createItemStack(player); // todo update placeholders after the fact. but when?

        int desiredSlot = accessItem.getSlot();
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
                player.getInventory().setHeldItemSlot(accessItem.getSlot());
            }
            return true;
        } else {
            // todo: send message about no success
            return false;
        }
    }
}
