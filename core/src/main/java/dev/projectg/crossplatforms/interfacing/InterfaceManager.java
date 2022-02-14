package dev.projectg.crossplatforms.interfacing;

import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.Player;
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
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class InterfaceManager {

    public static final String PLAYER_PREFIX = "player;";
    public static final String OP_PREFIX = "op;";
    public static final String CONSOLE_PREFIX = "console;";

    private final ServerHandler serverHandler;
    private final BedrockHandler bedrockHandler;

    @Getter
    private final BedrockFormRegistry bedrockRegistry;

    @Getter
    private final JavaMenuRegistry javaRegistry;

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
     * @param player The {@link Player} to send the form to
     * @param id The name of the form or menu to open
     */
    public void sendInterface(@Nonnull Player player, @Nonnull String id) {
        UUID uuid = player.getUuid();

        BedrockForm form = bedrockRegistry.getForm(id);
        JavaMenu menu = javaRegistry.getMenu(id);

        if (bedrockHandler.isBedrockPlayer(uuid)) {
            if (form != null) {
                // form exists
                if (player.hasPermission(form.permission(Interface.Limit.USE))) {
                    form.send(player);
                } else {
                    player.sendMessage("You don't have permission to use: " + id);
                }
            } else if (menu != null) {
                if (menu.isAllowBedrock()) {
                    // menu exists and BE is allowed
                    if (player.hasPermission(menu.permission(Interface.Limit.USE))) {
                        menu.send(player);
                    } else {
                        player.sendMessage("You don't have permission to use: " + id);
                    }
                } else {
                    // menu exists and BE is not allowed
                    player.sendMessage("That menu is only available to Java Edition players.");
                }
            } else {
                player.sendMessage("'" + id + "' doesn't exist.");
            }
        } else {
            if (menu != null) {
                if (player.hasPermission(menu.permission(Interface.Limit.USE))) {
                    menu.send(player);
                } else {
                    player.sendMessage("You don't have permission to use: " + id);
                }
            } else {
                player.sendMessage("'" + id + "' doesn't exist.");
            }
        }
    }

    /**
     * Process a command and run it.
     * If the command is prefixed with "player;" the command will be run as the player given, which CANNOT be null.
     * If the command is prefixed with "console;" the command will be run as the console.
     *
     * @param command The command to run
     * @param player the Player to run the command as, if prefixed with "player;"
     */
    public void runCommand(@Nonnull String command, @Nonnull UUID player) {
        Objects.requireNonNull(command);

        String executableCommand;
        if (command.startsWith(PLAYER_PREFIX) || command.startsWith(CONSOLE_PREFIX) || command.startsWith(OP_PREFIX)) {
            // Split the input into two strings between ";" and get the second string
            executableCommand = command.split(";", 2)[1].trim();
        } else {
            executableCommand = command;
        }

        if (command.startsWith(PLAYER_PREFIX)) {
            serverHandler.dispatchCommand(player, executableCommand, false);
        } else if (command.startsWith(OP_PREFIX)) {
            serverHandler.dispatchCommand(player, executableCommand, true);
        } else {
            serverHandler.dispatchCommand(executableCommand);
        }
    }
}
