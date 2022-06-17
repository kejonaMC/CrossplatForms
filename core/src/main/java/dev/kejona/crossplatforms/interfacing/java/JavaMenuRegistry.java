package dev.projectg.crossplatforms.interfacing.java;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.permission.Permission;
import dev.projectg.crossplatforms.reloadable.Reloadable;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class JavaMenuRegistry implements Reloadable {

    /**
     * If java menus are enabled. may be false if disabled in the config or if all forms failed to load.
     */
    private boolean enabled = false;
    private final Map<String, JavaMenu> menus = new HashMap<>();

    private final ConfigManager configManager;
    private final ServerHandler serverHandler;

    public JavaMenuRegistry(ConfigManager configManager, ServerHandler serverHandler) {
        this.configManager = configManager;
        this.serverHandler = serverHandler;
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
            for (String identifier : config.getMenus().keySet()) {
                JavaMenu menu = config.getMenus().get(identifier);
                menus.put(identifier, menu);

                menu.generatePermissions(config);
                for (Permission entry : menu.getPermissions().values()) {
                    serverHandler.registerPermission(entry);
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
                    serverHandler.unregisterPermission(permission.key());
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
    public JavaMenu getMenu(@Nullable String menuName) {
        return menus.get(menuName);
    }
}
