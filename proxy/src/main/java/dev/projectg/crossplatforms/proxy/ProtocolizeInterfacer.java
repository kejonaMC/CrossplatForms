package dev.projectg.crossplatforms.proxy;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.java.ItemButton;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;

import java.util.Locale;
import java.util.Map;

public class ProtocolizeInterfacer extends InterfaceManager {

    private final Logger logger;
    private final ProtocolizePlayerProvider playerProvider;

    public ProtocolizeInterfacer() {
        this.logger = Logger.getLogger();
        this.playerProvider = Protocolize.playerProvider();
    }

    @Override
    public void sendMenu(FormPlayer player, JavaMenu source) {
        // construct the inventory. todo: validate size
        InventoryType inventoryType;
        int size = source.getSize();
        if (size == 5) {
            inventoryType = InventoryType.HOPPER;
        } else {
            inventoryType = InventoryType.chestInventoryWithSize(size);
        }

        Inventory inventory = new Inventory(inventoryType);
        inventory.title(source.getTitle());

        Map<Integer, ItemButton> buttons = source.getButtons();
        for (Map.Entry<Integer, ItemButton> entry : buttons.entrySet()) {
            Integer slot = entry.getKey();
            ItemButton button = entry.getValue();
            // determine the material for this button
            String material = button.getMaterial().toUpperCase(Locale.ROOT).trim();
            ItemType type;
            try {
                type = ItemType.valueOf(material);
            } catch (IllegalArgumentException e) {
                logger.severe("Java Button: " + source.getIdentifier() + "." + slot + " will be stone because '" + material + "' failed to map to a valid Spigot Material.");
                type = ItemType.STONE;
            }
            // construct itemstack
            final ItemStack item = new ItemStack(type);
            item.displayName(button.getDisplayName());
            button.getLore().forEach(item::addToLore);
            inventory.item(slot, item); // add itemstack to inventory
        }

        ProtocolizePlayer protocolizePlayer = playerProvider.player(player.getUuid());
        /**
        inventory.onClick(click -> {
            int realSize = inventory.type().getTypicalSize(protocolizePlayer.protocolVersion());

            ClickType clickType = click.clickType();
            if (click.slot() >= 0 && click.slot() < realSize) {
                // click is within our menu
                boolean rightClick;
                switch (clickType) {
                    case RIGHT_CLICK:
                    case SHIFT_RIGHT_CLICK:
                        source.process(click.slot(), true, player);
                        break;
                    case LEFT_CLICK:
                    case SHIFT_LEFT_CLICK:
                        source.process(click.slot(), false, player);
                        break;
                }
            }
        });
        */
        protocolizePlayer.openInventory(inventory);
    }
}
