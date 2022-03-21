package dev.projectg.crossplatforms.interfacing.java;

import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class JavaMenu extends Interface {

    public static final String TYPE = "java_menu";

    public static final int MAX_SIZE = 54;
    public static final int HOPPER_SIZE = 5;

    protected transient final String permissionBase = Constants.Id() + ".menu.";

    private boolean allowBedrock = false;

    private int size = 5; // Hopper size
    private Map<Integer, ItemButton> buttons = Collections.emptyMap();

    /**
     * @param slot The inventory slot
     * @return If there is a button at the given inventory slot
     */
    public boolean isButton(int slot) {
        return buttons.get(slot) != null;
    }

    @Override
    public void send(@Nonnull FormPlayer recipient, @Nonnull InterfaceManager interfaceManager) {
        interfaceManager.sendMenu(recipient, this);
    }

    /**
     * Process a button click by a player in the menu.
     * @param slot The slot in the inventory. Nothing will happen if the slot does not contain a button.
     * @param rightClick True if it was a right click, false if a left click.
     * @param player the Player who clicked on the button.
     */
    public void process(int slot, boolean rightClick, @Nonnull FormPlayer player, @Nonnull InterfaceManager interfaceManager) {
        if (isButton(slot)) {
            ItemButton button = buttons.get(slot);
            List<Action> any = button.getAnyClick();

            Action.affectPlayer(player, any, interfaceManager, BedrockHandler.empty());
            if (rightClick) {
                Action.affectPlayer(player, button.getRightClick(), interfaceManager, BedrockHandler.empty());
            } else {
                Action.affectPlayer(player, button.getLeftClick(), interfaceManager, BedrockHandler.empty());
            }
        }
    }
}
