package dev.kejona.crossplatforms.proxy.item;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.item.ClickHandler;
import dev.kejona.crossplatforms.item.Inventory;
import dev.kejona.crossplatforms.item.InventoryController;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;

public class ProtocolizeInventoryController implements InventoryController {

    private final ProtocolizePlayerProvider players = Protocolize.playerProvider();

    @Override
    public void openInventory(FormPlayer recipient, Inventory container, ClickHandler clickHandler) {
        ProtocolizePlayer player = players.player(recipient.getUuid());
        dev.simplix.protocolize.api.inventory.Inventory inventory = container.castedHandle();

        inventory.onClick(click -> {
            int slot = click.slot();
            int realSize = inventory.type().getTypicalSize(player.protocolVersion());

            // Clicks are cancelled by default, so we don't have to do it explicitly.
            if (slot >= 0 && slot < realSize) {
                switch (click.clickType()) {
                    case RIGHT_CLICK:
                    case SHIFT_RIGHT_CLICK:
                        clickHandler.handle(slot, true);
                        break;
                    case LEFT_CLICK:
                    case SHIFT_LEFT_CLICK:
                        clickHandler.handle(slot, false);
                        break;
                }
            }
        });

        player.openInventory(inventory);
    }
}
