package dev.projectg.geyserhub.module.menu.java;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.config.ConfigId;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Objects;

public class JavaMenuListeners implements Listener {

    private final JavaMenuRegistry javaMenuRegistry;

    public JavaMenuListeners(@Nonnull JavaMenuRegistry javaMenuRegistry) {
        this.javaMenuRegistry = Objects.requireNonNull(javaMenuRegistry);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.SELECTOR);
        if (!config.getBoolean("Java-Selector.Enable")){
            return;
        }
        Player player = (Player) event.getWhoClicked();
        SelectorLogger logger = SelectorLogger.getLogger();

        ItemStack item = event.getCurrentItem();
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String menuName = meta.getPersistentDataContainer().get(JavaMenu.MENU_NAME_KEY, JavaMenu.MENU_NAME_TYPE);
                if (menuName != null) {
                    JavaMenu menu = javaMenuRegistry.getMenu(menuName);
                    if (menu == null) {
                        logger.warn("Failed to find any Java menu under the name '" + menuName + "' in order to process inventory click by player: " + player.getName());
                    } else {
                        menu.process(event.getSlot(), event.isRightClick(), player);
                    }
                }
            }
        }
    }
}