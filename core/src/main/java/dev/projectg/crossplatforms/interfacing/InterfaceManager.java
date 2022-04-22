package dev.projectg.crossplatforms.interfacing;

import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public abstract class InterfaceManager {

    public static final String PLAYER_PREFIX = "player;";
    public static final String OP_PREFIX = "op;";
    public static final String CONSOLE_PREFIX = "console;";

    private final ServerHandler serverHandler;
    private final BedrockHandler bedrockHandler;
    protected final BedrockFormRegistry bedrockRegistry;
    protected final JavaMenuRegistry javaRegistry;

    /**
     * Get an interface to fetch
     * @param name The named identifier of the interface
     * @param bedrock true if the interface is for a bedrock player
     * @return Always returns null or a {@link JavaMenu} if bedrock is false. May return null or a {@link BedrockForm} if
     * bedrock is true, as well as {@link JavaMenu} if the JavaMenu allows bedrock players. Prioritizes BedrockForms over
     * JavaMenus.
     */
    @Nullable
    public Interface getInterface(String name, boolean bedrock) {
        if (bedrock) {
            BedrockForm form = bedrockRegistry.getForm(name);
            if (form == null) {
                JavaMenu menu = javaRegistry.getMenu(name);
                if (menu != null && menu.isAllowBedrock()) {
                    return menu;
                } else {
                    return null;
                }
            } else {
                return form;
            }
        } else {
            return javaRegistry.getMenu(name);
        }
    }

    /**
     * @return A list of all forms and menus registered. This list is not backed.
     */
    @Nonnull
    public List<Interface> getInterfaces(boolean bedrock) {
        List<Interface> list = new ArrayList<>();
        if (bedrock) {
            list.addAll(bedrockRegistry.getForms().values());
            for (JavaMenu menu : javaRegistry.getMenus().values()) {
                if (menu.isAllowBedrock()) {
                    list.add(menu);
                }
            }
        } else {
            list.addAll(javaRegistry.getMenus().values());
        }

        return list;
    }

    /**
     * Sends a given form, identified by its name, to a BE or JE player.
     * If the form/menu doesn't exist or they don't have have the {@link Interface.Limit#USE} permission,
     * they will be told so,
     * @param player The {@link FormPlayer} to send the form to
     * @param id The name of the form or menu to open
     */
    public void sendInterface(@Nonnull FormPlayer player, @Nonnull String id) {
        UUID uuid = player.getUuid();

        BedrockForm form = bedrockRegistry.getForm(id);
        JavaMenu menu = javaRegistry.getMenu(id);

        if (bedrockHandler.isBedrockPlayer(uuid)) {
            if (form != null) {
                // form exists
                if (player.hasPermission(form.permission(Interface.Limit.USE))) {
                    form.send(player, this);
                } else {
                    denyPermission(player, id);
                }
            } else if (menu != null) {
                if (menu.isAllowBedrock()) {
                    // menu exists and BE is allowed
                    if (player.hasPermission(menu.permission(Interface.Limit.USE))) {
                        menu.send(player, this);
                    } else {
                        denyPermission(player, id);
                    }
                } else {
                    // menu exists and BE is not allowed
                    player.warn("That menu is only available to Java Edition players.");
                }
            } else {
                denyBadTarget(player, id);
            }
        } else {
            if (menu != null) {
                if (player.hasPermission(menu.permission(Interface.Limit.USE))) {
                    menu.send(player, this);
                } else {
                    denyPermission(player, id);
                }
            } else {
                denyBadTarget(player, id);
            }
        }
    }

    private static void denyPermission(FormPlayer player, String name) {
        player.warn("You don't have permission to use: " + name);
    }

    private static void denyBadTarget(FormPlayer player, String name) {
        player.warn("'" + name + "' doesn't exist.");
    }

    public abstract void sendMenu(FormPlayer player, JavaMenu menu);

    public abstract boolean supportsMenus();
}
