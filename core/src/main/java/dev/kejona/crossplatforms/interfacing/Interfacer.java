package dev.kejona.crossplatforms.interfacing;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;
import dev.kejona.crossplatforms.interfacing.java.JavaMenuRegistry;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Interfacer {

    protected BedrockFormRegistry bedrockRegistry;
    protected JavaMenuRegistry javaRegistry;

    public void load(BedrockFormRegistry bedrockRegistry, JavaMenuRegistry javaRegistry) {
        this.bedrockRegistry = bedrockRegistry;
        this.javaRegistry = javaRegistry;
    }

    /**
     * Get an interface to fetch
     * @param name The named identifier of the interface
     * @param bedrock true if the interface is for a bedrock player
     * @return Always returns null or a {@link JavaMenu} if bedrock is false. May return null or a {@link BedrockForm} if
     * bedrock is true, as well as {@link JavaMenu} if the JavaMenu allows bedrock players. Prioritizes BedrockForms over
     * JavaMenus.
     */
    @Nullable
    public Interface getInterface(@Nullable String name, boolean bedrock) {
        ensureLoaded();

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
        ensureLoaded();

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

    private void ensureLoaded() {
        if (bedrockRegistry == null || javaRegistry == null) {
            throw new IllegalStateException("Interfacer has not yet been loaded with registries");
        }
    }

    public abstract void sendMenu(FormPlayer player, JavaMenu menu);
}
