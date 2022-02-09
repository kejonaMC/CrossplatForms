package dev.projectg.crossplatforms.item;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.Platform;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.SpigotPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@AllArgsConstructor
public class AccessItemListeners implements Listener {

    private final Logger logger = Logger.getLogger();
    private final InterfaceManager interfaceManager;
    private final AccessItemRegistry registry;
    private final BedrockHandler bedrockHandler;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) { // opening menus through access items
        Action action = event.getAction();
        if (action != Action.PHYSICAL) {
            ItemStack item = event.getItem();
            if (item != null) {
                String id = AccessItemRegistry.getItemId(item);
                if (id != null) {
                    // Don't allow using the item to break blocks
                    // If it was a right click, using the access item should be the only behaviour
                    event.setCancelled(true);

                    if (registry.isEnabled()) {
                        AccessItem access = registry.getItem(id);
                        Player player = event.getPlayer();
                        if (access == null) {
                            // item is no longer registered
                            player.getInventory().remove(item);
                        } else if (player.hasPermission(access.permission(AccessItem.Limit.POSSESS))) {
                            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                                access.trigger(player, interfaceManager);
                            }
                        } else {
                            player.sendMessage("You don't have permission to have that.");
                            player.getInventory().remove(item);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) { // keep the access items in place
        // todo: don't allow duplication for creative players
        ItemStack item = event.getCurrentItem();
        if (item != null) {
            String id = AccessItemRegistry.getItemId(item);
            if (id != null) {
                if (registry.isEnabled()) {
                    HumanEntity human = event.getWhoClicked();
                    AccessItem access = registry.getItem(id);
                    if (access == null) {
                        // restrict items that no longer exist
                        event.setCancelled(true);
                    } else if (!human.hasPermission(access.permission(AccessItem.Limit.POSSESS))) {
                        human.sendMessage("You don't have permission to have that.");
                        human.getInventory().remove(item);
                    } else if (!human.hasPermission(access.permission(AccessItem.Limit.MOVE))) {
                        event.setCancelled(true);
                    }
                } else {
                    // restrict items even if the registry is disabled
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void PlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) { // Don't allow putting it in the offhand
        ItemStack item = event.getOffHandItem();
        if (item != null && AccessItemRegistry.getItemId(item) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) { // Stop players without possession permission to pickup items
        if (event.getEntity() instanceof Player player) {
            ItemStack item = event.getItem().getItemStack();
            String id = AccessItemRegistry.getItemId(item);
            if (id != null) {
                AccessItem access = registry.getItem(id);
                if (access == null) {
                    event.setCancelled(true);
                } else if (!player.hasPermission(access.permission(AccessItem.Limit.POSSESS))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) { // restricting dropping
        ItemStack item = event.getItemDrop().getItemStack();
        String id = AccessItemRegistry.getItemId(item);
        if (id != null) {
            AccessItem access = registry.getItem(item);
            if (access == null) {
                event.setCancelled(true);
            } else {
                Player player = event.getPlayer();
                if (player.hasPermission(access.permission(AccessItem.Limit.DROP))) {
                    if (!player.hasPermission(access.permission(AccessItem.Limit.PRESERVE))) {
                        event.getItemDrop().remove();
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) { // restricting dropping
        Player player = event.getEntity();
        for (ItemStack item : event.getDrops()) {
            String id = AccessItemRegistry.getItemId(item);
            if (id != null) {
                AccessItem access = registry.getItem(id);
                if (access != null) {
                    if (!player.hasPermission(access.permission(AccessItem.Limit.PRESERVE))) {
                        event.getDrops().remove(item);
                    }
                }
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
                AccessItem access = registry.getItem(item);
                if (access != null && !access.isPersist()) {
                    player.getInventory().remove(item);
                    logger.debug("Removing access item %s from %s".formatted(access.getIdentifier(), player.getName()));
                }
            }
        }
    }

    private void regive(Player player, Predicate<AccessItem> give) {
        List<String> contained = new ArrayList<>(); // Access items the player already has and that will not be removed

        // Remove any access items that are now longer allowed
        for (ItemStack item : player.getInventory()) {
            if (item != null) {
                AccessItem access = registry.getItem(item);
                if (access != null) {
                    if (player.hasPermission(access.permission(AccessItem.Limit.POSSESS))) {
                        contained.add(access.getIdentifier());
                        logger.debug("%s is keeping access item %s".formatted(player.getName(), access.getIdentifier()));
                    } else {
                        player.getInventory().remove(item);
                        logger.debug("Removed %s from %s because they don't have permission for it".formatted(access.getIdentifier(), player.getName()));
                    }
                }
            }
        }

        // Give any access items that should be given
        boolean changedHand = false; // If we have changed the item the player is holding
        for (AccessItem access : registry.getItems().values()) {
            if (give.test(access) && Platform.matches(player.getUniqueId(), access.getPlatform(), bedrockHandler) && player.hasPermission(access.permission(AccessItem.Limit.EVENT))) {
                if (!contained.contains(access.getIdentifier())) {
                    if (registry.setHeldSlot() && !changedHand) {
                        giveAccessItem(player, access, true);
                        changedHand = true;
                        logger.debug("Set held slot to " + access.getSlot());
                    } else {
                        giveAccessItem(player, access, false);
                    }

                    logger.debug("Gave access item %s to %s".formatted(access.getIdentifier(), player.getName()));
                } else {
                    logger.debug("%s has permission for access item %s, but they already have it".formatted(player.getName(), access.getIdentifier()));
                }
            }
        }
    }

    /**
     * Gives a player an access item
     * @param player The player to give the access item to
     * @param accessItem The access item to give
     * @param setHeldSlot True if the player's selected item should be forced to the access item
     * @return True if the access item was successfully given. False if the inventory was too full.
     */
    public static boolean giveAccessItem(Player player, AccessItem accessItem, boolean setHeldSlot) {
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
