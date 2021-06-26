package dev.projectg.geyserhub.module.menu.java;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.module.menu.MenuUtils;
import dev.projectg.geyserhub.utils.PlaceholderUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class JavaMenu {

    private static final NamespacedKey BUTTON_KEY = new NamespacedKey(GeyserHubMain.getInstance(), "geyserHubButton");

    private final SelectorLogger logger;

    public final boolean isEnabled;
    @Nonnull private final String menuName;

    private String title;
    private int size;
    private Map<Integer, ItemButton> buttons;

    protected JavaMenu(@Nonnull ConfigurationSection configSection) {
        logger = SelectorLogger.getLogger();
        Objects.requireNonNull(configSection);
        menuName = configSection.getName();

        // Get the inventory title and size
        if (configSection.contains("Title") && configSection.contains("Size") && configSection.isInt("Size")) {
            title = Objects.requireNonNull(configSection.getString("Title"));
            size = configSection.getInt("Size");
            logger.debug("Java Menu: " + menuName + " has Title: " + title + " Size: " + size);
        } else {
            logger.warn("Java Menu: " + menuName + " does not contain a Title or Size value, unable to create menu");
            isEnabled = false;
            return;
        }

        // Get the Buttons
        if (configSection.contains("Buttons", true) && configSection.isConfigurationSection("Buttons")) {
            ConfigurationSection buttonSection = configSection.getConfigurationSection("Buttons");
            Objects.requireNonNull(buttonSection);
            Map<Integer, ItemButton> buttons = getAllButtons(buttonSection);
            if (buttons.isEmpty()) {
                logger.warn("Failed to create any valid buttons of Bedrock form: " + menuName + "! All listed buttons have a malformed section!");
                isEnabled = false;
                return;
            } else {
                logger.debug("Finished adding buttons to Java menu: " + menuName);
            }
            this.buttons = buttons;
        } else {
            logger.warn("Java Menu: " + menuName + " does not contain a Buttons section, unable to create form.");
            isEnabled = false;
            return;
        }

        // Make sure the inventory will be able to hold all the buttons
        int highestGivenSlot = 0;
        for (Integer slot: buttons.keySet()) {
            highestGivenSlot = Math.max(slot, highestGivenSlot);
        }
        int minimumSize = highestGivenSlot + 1;
        if (minimumSize > size) {
            logger.warn("Java Menu: " + menuName + " has a button with slot " + highestGivenSlot + ", but the inventory size is only " + size + ". Increasing the size.");
            size = minimumSize;
        }

        // Make sure that the inventory size is a multiple of 9
        if (size % 9 != 0) {
            // Divide the size by 9, round the ratio up to the next int value, then multiply by 9 to get the closest higher number that is a multiple of 9
            size = (int) (9*(Math.ceil(Math.abs(size/9))));
        }

        isEnabled = true;
    }

    @Nonnull
    private Map<Integer, ItemButton> getAllButtons(@Nonnull ConfigurationSection configSection) {
        logger.debug("Getting buttons for java form: " + menuName);

        // Get all the defined buttons in the buttons section
        Set<String> allButtonIds = configSection.getKeys(false);
        if (allButtonIds.isEmpty()) {
            logger.warn("No buttons were listed for form: " + menuName);
            return Collections.emptyMap();
        }

        // Create a list of buttons. For every defined button with a valid server or command configuration, we add its button.
        Map<Integer, ItemButton> compiledButtons = new HashMap<>();
        for (String buttonId : allButtonIds) {

            // Make sure its a configuration section (we know it exists)
            ConfigurationSection buttonInfo = configSection.getConfigurationSection(buttonId);
            if (buttonInfo == null) {
                logger.warn("Java Button: " + menuName + "." + buttonId + " was not added because it is not a configuration section!");
                continue;
            }
            // Make sure the key is a integer (the slot value of the button item)
            int slot;
            try {
                slot = Integer.parseUnsignedInt(buttonId);
            } catch (NumberFormatException e) {
                logger.warn("Java Button: " + menuName + "." + buttonId + " was not added because its config name is not a positive integer!");
                continue;
            }

            ItemButton button = getButton(buttonInfo);
            if (button != null) {
                compiledButtons.put(slot, button);
                logger.debug("Java Button: " + menuName + "." + buttonId + " was successfully added.");
            }
        }
        return compiledButtons;
    }

    @Nullable
    private ItemButton getButton(@Nonnull ConfigurationSection buttonInfo) {
        String buttonId = buttonInfo.getName();

        String displayName;
        if (buttonInfo.contains("Display-Name", true) && buttonInfo.isString("Display-Name")) {
            displayName = buttonInfo.getString("Display-Name");
            Objects.requireNonNull(displayName);
            logger.debug("Java Button: " + menuName + "." + buttonId + " has Display-Name: " + displayName);
        } else {
            logger.warn("Java Button: " + menuName + "." + buttonId + " does not contain a valid Button-Text value, not adding.");
            return null;
        }

        Material material;
        if (buttonInfo.contains("Material") && buttonInfo.isString("Material")) {
            String materialName = buttonInfo.getString("Material");
            Objects.requireNonNull(materialName);
            material = Material.getMaterial(materialName, false);
            if (material == null) {
                material = Material.getMaterial(materialName, true);
                if (material == null) {
                    logger.warn("Java Button: " + menuName + "." + buttonId + " was not added because the Material it provided was not valid.");
                    return null;
                } else {
                    logger.warn("Java Button: " + menuName + "." + buttonId + "specified a legacy Material, please update it: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");
                }
            }
        } else {
            logger.warn("Java Button: " + menuName + "." + buttonId + " was not added because it is does not contain a Material value!");
            return null;
        }

        // Create the button
        ItemButton button = new ItemButton(displayName, material);

        // Set server(s) and commands
        ConfigurationSection rightClick = null;
        if (buttonInfo.contains("Right-Click") && buttonInfo.isConfigurationSection("Right-Click")) {
            rightClick = buttonInfo.getConfigurationSection("Right-Click");
            Objects.requireNonNull(rightClick);
            button.getRightClickButton().setCommands(MenuUtils.getCommands(rightClick));
            button.getRightClickButton().setServer(MenuUtils.getServer(rightClick));
        }
        ConfigurationSection leftClick = null;
        if (buttonInfo.contains("Left-Click") && buttonInfo.isConfigurationSection("Left-Click")) {
            leftClick = buttonInfo.getConfigurationSection("Left-Click");
            Objects.requireNonNull(leftClick);
            button.getLeftClickButton().setCommands(MenuUtils.getCommands(leftClick));
            button.getLeftClickButton().setServer(MenuUtils.getServer(leftClick));
        }

        if (buttonInfo.contains("Any-Click") && buttonInfo.isConfigurationSection("Any-Click")) {
            if (rightClick != null || leftClick != null) {
                logger.warn("Java Button: " + menuName + "." + buttonId + " Cannot define both Any-Click behaviour and also Right/Left-Click behaviour! Ignoring Any-Click section.");
            } else {
                ConfigurationSection anyClick = buttonInfo.getConfigurationSection("Any-Click");
                Objects.requireNonNull(anyClick);
                button.getRightClickButton().setCommands(MenuUtils.getCommands(anyClick));
                button.getRightClickButton().setServer(MenuUtils.getServer(anyClick));
                button.getLeftClickButton().setCommands(MenuUtils.getCommands(anyClick));
                button.getLeftClickButton().setServer(MenuUtils.getServer(anyClick));
            }
        }

        return button;
    }

    protected void sendMenu(@Nonnull Player player) {
        if (!isEnabled) {
            throw new AssertionError("Tried to send Java Menu: " + menuName + " to a player but the form was not enabled");
        }

        Inventory selectorGUI = Bukkit.createInventory(player, size, PlaceholderUtils.setPlaceholders(player, title));

        for (Integer slot : buttons.keySet()) {
            ItemButton button = buttons.get(slot);

            // Construct the item
            ItemStack serverStack = new ItemStack(button.getMaterial());
            ItemMeta itemMeta = serverStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(PlaceholderUtils.setPlaceholders(player, button.getDisplayName()));
                itemMeta.setLore(PlaceholderAPI.setPlaceholders(player, button.getLore()));
                itemMeta.getPersistentDataContainer().set(BUTTON_KEY, PersistentDataType.STRING, menuName);
                serverStack.setItemMeta(itemMeta);
            } else {
                logger.warn("Java Button: " + menuName + "." + slot + " with Material: " + button.getMaterial() + " returned null ItemMeta, failed to set display name or lore. The button will not have any results.");
            }

            selectorGUI.setItem(slot, serverStack);
        }

        player.openInventory(selectorGUI);
    }
}
