package dev.projectg.crossplatforms.form.java;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.config.mapping.java.MenuConfig;
import dev.projectg.crossplatforms.config.mapping.java.Menu;
import dev.projectg.crossplatforms.reloadable.Reloadable;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JavaMenuRegistry implements Reloadable {

    /**
     * If java menus are enabled. may be false if disabled in the config or if all forms failed to load.
     */
    private boolean isEnabled;
    private final Map<String, Menu> enabledMenus = new HashMap<>();

    public JavaMenuRegistry() {
        ReloadableRegistry.registerReloadable(this);
        isEnabled = load();
    }

    private boolean load() {
        MenuConfig config = CrossplatForms.getInstance().getConfigManager().getConfig(MenuConfig.class);
        enabledMenus.clear();
        if (config.isEnable()) {
            enabledMenus.putAll(config.getElements());
        }
        return true;
    }

    /**
     * @return True, if Java menus are enabled.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Get a Java menu, based off its name.
     * @param menuName The menu name
     * @return the JavaMenu, null if it doesn't exist.
     */
    @Nullable
    public Menu getMenu(@Nonnull String menuName) {
        Objects.requireNonNull(menuName);
        return enabledMenus.get(menuName);
    }

    /**
     * Attempt to retrieve the menu that an ItemStack points to
     * @param itemStack The ItemStack to check. If it contains null ItemMeta, this will return null.
     * @return The menu if the ItemStack contained the menu name and the menu exists. If no menu name was contained or the menu contained doesn't exist, this will return null.
     */
    @Nullable
    public Menu getMenu(@Nonnull ItemStack itemStack) {
        String menuName = getMenuName(itemStack);
        if (menuName == null) {
            return null;
        } else {
            return getMenu(menuName);
        }
    }

    /**
     * Attempt to retrieve the menu name that an ItemStack is contained in
     * @param itemStack The ItemStack to check
     * @return The menu name if the ItemStack contained the menu name, null if not. ItemStacks with null ItemMeta will always return null.
     */
    @Nullable
    public String getMenuName(@Nonnull ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            return meta.getPersistentDataContainer().get(Menu.BUTTON_KEY, Menu.BUTTON_KEY_TYPE);
        }
        return null;
    }

    @Override
    public boolean reload() {
        isEnabled = load();
        return true;
    }
}
