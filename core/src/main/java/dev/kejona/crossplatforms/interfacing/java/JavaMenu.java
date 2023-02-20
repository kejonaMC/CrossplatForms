package dev.kejona.crossplatforms.interfacing.java;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Constants;
import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.Interface;
import dev.kejona.crossplatforms.item.ClickHandler;
import dev.kejona.crossplatforms.item.Inventory;
import dev.kejona.crossplatforms.item.InventoryController;
import dev.kejona.crossplatforms.item.InventoryFactory;
import dev.kejona.crossplatforms.item.InventoryLayout;
import dev.kejona.crossplatforms.resolver.Resolver;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class JavaMenu extends Interface {

    public static final String TYPE = "java_menu";

    protected final transient String permissionBase = Constants.Id() + ".menu.";

    @Inject
    protected transient InventoryFactory factory;
    @Inject
    protected transient InventoryController controller;

    private boolean allowBedrock = false;

    private int size = 5; // Hopper size by default
    private InventoryLayout type = InventoryLayout.CHEST;

    private Map<Integer, ItemButton> buttons = Collections.emptyMap();

    /**
     * @param slot The inventory slot
     * @return If there is a button at the given inventory slot
     */
    public boolean isButton(int slot) {
        return buttons.get(slot) != null;
    }

    @Override
    public void send(@Nonnull FormPlayer recipient, @Nonnull Resolver resolver) {
        String title = resolver.apply(this.title);
        Inventory inventory;
        if (type == InventoryLayout.CHEST) {
            if (size == 5) {
                // Hopper
                inventory = factory.inventory(title, InventoryLayout.HOPPER);
            } else {
                inventory = factory.chest(title, size);
            }
        } else {
            inventory = factory.inventory(title, type);
        }
        // todo: size validation/restraint
        for (Integer slot : buttons.keySet()) {
            ItemButton button = buttons.get(slot);
            inventory.setSlot(slot, button.convertAndResolve(recipient, resolver));
        }

        ClickHandler clickHandler = (slot, rightClick) -> {
            if (isButton(slot)) {
                ItemButton button = buttons.get(slot);

                affectPlayer(recipient, button.getAnyClick(), resolver);
                if (rightClick) {
                    affectPlayer(recipient, button.getRightClick(), resolver);
                } else {
                    affectPlayer(recipient, button.getLeftClick(), resolver);
                }
            }
        };

        controller.openInventory(recipient, inventory, clickHandler);
    }

    private void affectPlayer(FormPlayer player, Iterable<Action<? super JavaMenu>> actions, Resolver resolver) {
        actions.forEach(a -> a.affectPlayer(player, resolver, this));
    }
}
