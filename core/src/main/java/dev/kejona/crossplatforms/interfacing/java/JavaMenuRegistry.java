package dev.kejona.crossplatforms.interfacing.java;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.permission.Permission;
import dev.kejona.crossplatforms.permission.Permissions;
import dev.kejona.crossplatforms.reloadable.Reloadable;
import dev.kejona.crossplatforms.reloadable.ReloadableRegistry;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JavaMenuRegistry implements Reloadable {


    private final ConfigManager configManager;
    private final Permissions permissions;

    @Getter
    private final Map<String, JavaMenu> menus = new HashMap<>();

    /**
     * If java menus are enabled. may be false if disabled in the config or if all forms failed to load.
     */
    @Getter
    private boolean enabled = false;

    public JavaMenuRegistry(ConfigManager configManager, Permissions permissions) {
        this.configManager = configManager;
        this.permissions = permissions;
        ReloadableRegistry.register(this);
        load();
    }

    private void load() {
        menus.clear();

        if (!configManager.getConfig(MenuConfig.class).isPresent()) {
            enabled = false;
            Logger.get().warn("Menu config is not present, not enabling menus.");
            return;
        }

        MenuConfig config = configManager.getConfig(MenuConfig.class).get();
        enabled = config.isEnable();
        if (enabled) {
            Set<Permission> permissions = new HashSet<>();

            for (String identifier : config.getMenus().keySet()) {
                JavaMenu menu = config.getMenus().get(identifier);
                menus.put(identifier, menu);

                menu.generatePermissions(config);
                permissions.addAll(menu.getPermissions().values());
            }

            this.permissions.registerPermissions(permissions);
        }
    }

    @Override
    public boolean reload() {
        load();
        return true;
    }

    /**
     * Get a Java menu, based off its name.
     * @param menuName The menu name
     * @return the JavaMenu, null if it doesn't exist.
     */
    @Nullable
    public JavaMenu getMenu(@Nullable String menuName) {
        return menus.get(menuName);
    }
}
