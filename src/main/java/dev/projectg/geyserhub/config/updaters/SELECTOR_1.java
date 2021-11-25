package dev.projectg.geyserhub.config.updaters;

import dev.projectg.geyserhub.config.ConfigManager;
import dev.projectg.geyserhub.config.ConfigUpdater;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Objects;

public class SELECTOR_1 implements ConfigUpdater {

    @Override
    public boolean update(ConfigurationSection config) throws IllegalArgumentException {
        if (!config.contains("Config-Version", true) || config.getInt("Config-Version") != 1) {
            throw new IllegalArgumentException("Config is not version 1");
        }

        if (!config.contains("Selector-Item", true) || !config.isConfigurationSection("Selector-Item")) {
            throw new IllegalArgumentException("Config does not contain configuration section 'Selector-Item'");
        }

        // Get the mutable ConfigurationSection
        ConfigurationSection items = config.getConfigurationSection("Selector-Item");
        Objects.requireNonNull(items);
        // Remove it from the parent afterwards, since it must be renamed
        config.set("Selector-Item", null);

        // A map containing the same information as the configuration section
        Map<String, Object> defaultItem = ConfigManager.asMap(items);
        defaultItem.put("Form", "default"); // new data

        // Clear the section
        for (String key : items.getKeys(false)) {
            // Remove the old single access item data
            items.set(key, null);
        }

        // Add the enable key and put the old single access item data in a subsection
        items.set("Enable", true); // new data
        items.createSection("Items.default", defaultItem); // re-insert the old data into the default access item

        // Add the Selector-Item section back in, under the new name
        config.createSection("Access-Items", ConfigManager.asMap(items));

        // bump version
        config.set("Config-Version", 2);

        return true;
    }
}
