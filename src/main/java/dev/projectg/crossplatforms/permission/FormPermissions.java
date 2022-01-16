package dev.projectg.crossplatforms.permission;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Defines permissions that allow using forms, menus, and access items, as well as their direct commands to access them.
 */
@Getter
@Accessors(fluent = true)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class FormPermissions {
    // todo: use this
    public static final FormPermissions DEFAULT = new FormPermissions();

    /**
     * True if the Player has permission to open the form/menu
     */
    private boolean use = true;

    /**
     * True if the player has permission to obtain the open the form/menu through a command they run themselves
     */
    private boolean command = false;
}
