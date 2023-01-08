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
    public void openInventory(FormPlayer recipient, JavaMenu source, dev.kejona.crossplatforms.item.Inventory container, Resolver resolver) {
        ProtocolizePlayer player = playerProvider.player(recipient.getUuid());
        Inventory inventory = container.castedHandle();

        if (!inventory.clickConsumers().isEmpty()) {
            Logger.get().severe("Cannot send menu '" + source.getIdentifier() + "' to " + recipient.getName() + " because the backing Protocolize inventory has already been shown to a different player.");
            Thread.dumpStack();
            return;
        }

        inventory.onClick(click -> {
            int realSize = inventory.type().getTypicalSize(player.protocolVersion());

            // Clicks are cancelled by default, so we don't have to do it explicitly.
            ClickType clickType = click.clickType();
            if (click.slot() >= 0 && click.slot() < realSize) {
                switch (clickType) {
                    case RIGHT_CLICK:
                    case SHIFT_RIGHT_CLICK:
                        source.process(click.slot(), true, recipient, resolver);
                        break;
                    case LEFT_CLICK:
                    case SHIFT_LEFT_CLICK:
                        source.process(click.slot(), false, recipient, resolver);
                        break;
                }
            }
        });

        player.openInventory(inventory);
    }
}
