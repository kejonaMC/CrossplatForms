package dev.projectg.crossplatforms.interfacing.java;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.permission.Permission;
import dev.projectg.crossplatforms.reloadable.Reloadable;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class JavaMenuRegistry implements Reloadable {

    /**
     * If java menus are enabled. may be false if disabled in the config or if all forms failed to load.
     */
    private boolean enabled;
    private final Map<String, JavaMenu> menus = new HashMap<>();

    public JavaMenuRegistry() {
        ReloadableRegistry.registerReloadable(this);
        load();
    }

    private void load() {
        CrossplatForms plugin = CrossplatForms.getInstance();
        MenuConfig config = plugin.getConfigManager().getConfig(MenuConfig.class);
        menus.clear();
        if (enabled = config.isEnable()) {
            for (String identifier : config.getMenus().keySet()) {
                JavaMenu menu = config.getMenus().get(identifier);
                menus.put(identifier, menu);

                menu.generatePermissions(config);
                for (Permission entry : menu.getPermissions().values()) {
                    plugin.getServerHandler().registerPermission(entry);
                }
            }
        }
    }

    @Override
    public boolean reload() {

        // Unregister permissions
        if (enabled) {
            for (Interface menu : menus.values()) {
                for (Permission permission : menu.getPermissions().values()) {
                    CrossplatForms.getInstance().getServerHandler().unregisterPermission(permission.key());
                }
            }
        }

        load();
        return true;
    }

    /**
     * Get a Java menu, based off its name.
     * @param menuName The menu name
     * @return the JavaMenu, null if it doesn't exist.
     */
    @Nullable
    public JavaMenu getMenu(@Nonnull String menuName) {
        Objects.requireNonNull(menuName);
        return menus.get(menuName);
    }

    /**
     * Attempt to retrieve the menu that an ItemStack points to
     * @param itemStack The ItemStack to check. If it contains null ItemMeta, this will return null.
     * @return The menu if the ItemStack contained the menu name and the menu exists. If no menu name was contained or the menu contained doesn't exist, this will return null.
     */
    @Nullable
    public JavaMenu getMenu(@Nonnull ItemStack itemStack) {
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
    public static String getMenuName(@Nonnull ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            return meta.getPersistentDataContainer().get(JavaMenu.BUTTON_KEY, JavaMenu.BUTTON_KEY_TYPE);
        }
        return null;
    }
}
