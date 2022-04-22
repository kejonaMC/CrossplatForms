package dev.projectg.crossplatforms.proxy;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.ItemButton;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;

import java.util.Locale;
import java.util.Map;

public class ProtocolizeInterfacer extends InterfaceManager {

    private final Logger logger;
    private final ProtocolizePlayerProvider playerProvider;

    public ProtocolizeInterfacer(ServerHandler serverHandler,
                                 BedrockHandler bedrockHandler,
                                 BedrockFormRegistry bedrockRegistry,
                                 JavaMenuRegistry javaRegistry) {
        super(serverHandler, bedrockHandler, bedrockRegistry, javaRegistry);
        this.logger = Logger.getLogger();
        this.playerProvider = Protocolize.playerProvider();
    }

    @Override
    public void sendMenu(FormPlayer player, JavaMenu source) {
        // construct the inventory. todo: validate size
        final InventoryType inventoryType;
        int size = source.getSize();
        if (size == 5) {
            inventoryType = InventoryType.HOPPER;
        } else {
            inventoryType = InventoryType.chestInventoryWithSize(size);
        }

        final Inventory inventory = new Inventory(inventoryType);
        inventory.title(source.getTitle());

        Map<Integer, ItemButton> buttons = source.getButtons();
        for (Map.Entry<Integer, ItemButton> entry : buttons.entrySet()) {
            final Integer slot = entry.getKey();
            final ItemButton button = entry.getValue();
            // determine the material for this button
            final String material = button.getMaterial().toUpperCase(Locale.ROOT).trim();
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

        inventory.onClick(click -> {
            final boolean rightClick;
            switch (click.clickType()) {
                case RIGHT_CLICK:
                case SHIFT_RIGHT_CLICK:
                    rightClick = true;
                    break;
                case LEFT_CLICK:
                case SHIFT_LEFT_CLICK:
                    rightClick = false;
                    break;
                default:
                    // not an appropriate click, cancel it
                    click.cancelled(true);
                    return;
            }
            // delegate action handling
            source.process(click.slot(), rightClick, player, this);
        });

        playerProvider.player(player.getUuid()).openInventory(inventory);
    }

    @Override
    public boolean supportsMenus() {
        return true;
    }
}
