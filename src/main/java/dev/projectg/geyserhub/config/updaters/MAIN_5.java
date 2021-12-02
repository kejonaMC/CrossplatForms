package dev.projectg.geyserhub.config.updaters;

import dev.projectg.geyserhub.config.ConfigUpdater;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class MAIN_5 implements ConfigUpdater {

    @Override
    public boolean update(ConfigurationSection config) throws IllegalArgumentException {
        if (!config.contains("Join-Teleporter.Coordinates", true)) {
            throw new IllegalArgumentException("Main config must contain Join-Teleporter config section and Coordinates string value");
        }

        ConfigurationSection teleporter = config.getConfigurationSection("Join-Teleporter");
        Objects.requireNonNull(teleporter);

        String composedCoords = Objects.requireNonNull(teleporter.getString("Coordinates"));
        String[] coordinates = composedCoords.split(";", 3);

        try {
            teleporter.set("X", Integer.parseInt(coordinates[0]));
            teleporter.set("Y", Integer.parseInt(coordinates[1]));
            teleporter.set("Z", Integer.parseInt(coordinates[2]));
            teleporter.set("Yaw", 0); // Default
            teleporter.set("Pitch", 0); // Default

            config.set("Join-Teleporter.Coordinates", null); // Remove
            config.set("Config-Version", 6);

            return true;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Config contains invalid coordinates value: string must be 3 integers separated by 2 semicolons.");
        }
    }
}
