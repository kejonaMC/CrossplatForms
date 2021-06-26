package dev.projectg.geyserhub.module.menu.java;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.config.ConfigId;
import dev.projectg.geyserhub.reloadable.Reloadable;
import dev.projectg.geyserhub.reloadable.ReloadableRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;

public class JavaMenuRegistry implements Reloadable {

    public static final String DEFAULT = "default";

    /**
     * If java menus are enabled. may be false if disabled in the config or if all forms failed to load.
     */
    private boolean isEnabled;
    private final Map<String, JavaMenu> enabledMenus = new HashMap<>();

    public JavaMenuRegistry() {
        ReloadableRegistry.registerReloadable(this);
        isEnabled = load();
    }

    private boolean load() {
        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.SELECTOR);
        SelectorLogger logger = SelectorLogger.getLogger();

        enabledMenus.clear();

        if (config.contains("Java-Selector", true) && config.isConfigurationSection("Java-Selector")) {
            ConfigurationSection selectorSection = config.getConfigurationSection("Java-Selector");
            Objects.requireNonNull(selectorSection);

            if (selectorSection.contains("Enable", true) && selectorSection.isBoolean("Enable")) {
                if (selectorSection.getBoolean("Enable")) {
                    if (selectorSection.contains("Menus", true) && selectorSection.isConfigurationSection("Menus")) {
                        ConfigurationSection menus = selectorSection.getConfigurationSection("Menus");
                        Objects.requireNonNull(menus);

                        boolean noSuccess = true;
                        boolean containsDefault = false;
                        for (String entry : menus.getKeys(false)) {
                            if (!menus.isConfigurationSection(entry)) {
                                logger.warn("Java menu with name " + entry + " is being skipped because it is not a configuration section");
                                continue;
                            }
                            ConfigurationSection formInfo = menus.getConfigurationSection(entry);
                            Objects.requireNonNull(formInfo);
                            JavaMenu menu = new JavaMenu(formInfo);
                            if (menu.isEnabled) {
                                enabledMenus.put(entry, menu);
                                noSuccess = false;
                            } else {
                                logger.warn("Not adding Java manu for config section: " + entry + " because there was a failure loading it.");
                            }
                            if ("default".equals(entry)) {
                                containsDefault = true;
                            }
                        }

                        if (!containsDefault) {
                            logger.warn("Failed to load a default Java menus! The Java Server Selector compass will not work and players will not be able to open the default form with \"/ghub\"");
                        }
                        if (noSuccess) {
                            logger.warn("Failed to load ALL Java menus, due to configuration error.");
                        } else {
                            logger.info("Valid Java menus are: " + enabledMenus.keySet());
                            return true;
                        }
                    }
                } else {
                    logger.debug("Not enabling Java menus because it is disabled in the config.");
                }
            } else {
                logger.warn("Not enabling Java menus because the Enable value is not present in the config.");
            }
        } else {
            logger.warn("Not enabling Java menus because the whole configuration section is not present.");
        }
        return false;
    }

    public void sendForm(@Nonnull Player player, @Nonnull String form) {
        enabledMenus.get(form).sendMenu(player);
    }

    public boolean isEnabled() {
        return isEnabled;
    }
    public List<String> getFormNames() {
        return new ArrayList<>(enabledMenus.keySet());
    }

    @Override
    public boolean reload() {
        isEnabled = load();
        return true;
    }
}
