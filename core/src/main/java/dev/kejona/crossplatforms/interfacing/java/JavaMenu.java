package dev.kejona.crossplatforms.interfacing.java;

import dev.kejona.crossplatforms.Constants;
import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.Interface;
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

    public static final int MAX_SIZE = 54; // todo: size validation
    public static final int HOPPER_SIZE = 5;

    protected final transient String permissionBase = Constants.Id() + ".menu.";

    private boolean allowBedrock = false;

    private int size = 5; // Hopper size by default
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
        interfacer.sendMenu(recipient, this, resolver);
    }

    /**
     * Process a button click by a player in the menu.
     * @param slot The slot in the inventory. Nothing will happen if the slot does not contain a button.
     * @param rightClick True if it was a right click, false if a left click.
     * @param player the Player who clicked on the button.
     */
    public void process(int slot, boolean rightClick, @Nonnull FormPlayer player, Resolver resolver) {
        if (isButton(slot)) {
            ItemButton button = buttons.get(slot);

            affectPlayer(player, button.getAnyClick(), resolver);
            if (rightClick) {
                affectPlayer(player, button.getRightClick(), resolver);
            } else {
                affectPlayer(player, button.getLeftClick(), resolver);
            }
        }
    }

    private void affectPlayer(FormPlayer player, Iterable<Action<? super JavaMenu>> actions, Resolver resolver) {
        actions.forEach(a -> a.affectPlayer(player, resolver, this));
    }
}
