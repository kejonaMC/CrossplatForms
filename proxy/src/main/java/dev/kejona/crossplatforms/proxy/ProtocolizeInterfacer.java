package dev.kejona.crossplatforms.proxy;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.interfacing.java.ItemButton;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Map;

public class ProtocolizeInterfacer extends Interfacer {

    private final Logger logger;
    private final ProtocolizePlayerProvider playerProvider;

    public ProtocolizeInterfacer() {
        this.logger = Logger.get();
        this.playerProvider = Protocolize.playerProvider();
    }

    @Override
    public void sendMenu(FormPlayer player, JavaMenu source, @Nonnull Resolver resolver) {
        // construct the inventory. todo: validate size
        // todo: apply placeholders and use args
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
            String material = button.getMaterial();
            ItemType type;
            if (material == null) {
                type = ItemType.STONE;
            } else {
                material = material.toUpperCase(Locale.ROOT).trim();
                try {
                    type = ItemType.valueOf(material);
                } catch (IllegalArgumentException e) {
                    logger.severe("Java Button: " + source.getIdentifier() + "." + slot + " will be stone because '" + material + "' failed to map to a valid Spigot Material.");
                    type = ItemType.STONE;
                }
            }

            // construct itemstack
            final ItemStack item = new ItemStack(type);
            item.displayName(button.getDisplayName());
            button.getLore().forEach(item::addToLore);
            inventory.item(slot, item); // add itemstack to inventory
        }

        ProtocolizePlayer protocolizePlayer = playerProvider.player(player.getUuid());

        inventory.onClick(click -> {
            int realSize = inventory.type().getTypicalSize(protocolizePlayer.protocolVersion());

            // Clicks are cancelled by default, so we don't have to do it explicitly.
            ClickType clickType = click.clickType();
            if (click.slot() >= 0 && click.slot() < realSize) {
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

        protocolizePlayer.openInventory(inventory);
    }
}
