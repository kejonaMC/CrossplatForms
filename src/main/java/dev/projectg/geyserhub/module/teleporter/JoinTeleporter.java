package dev.projectg.geyserhub.module.teleporter;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.config.ConfigId;
import dev.projectg.geyserhub.reloadable.Reloadable;
import dev.projectg.geyserhub.reloadable.ReloadableRegistry;
import dev.projectg.geyserhub.SelectorLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

public class JoinTeleporter implements Listener, Reloadable {

    private static final String COORDINATE_REGEX = "^(-\\d+|\\d+);(-\\d+|\\d+);(-\\d+|\\d+)$\n";

    private boolean enabled;
    private Location location;

    public JoinTeleporter() {
        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.MAIN);
        Objects.requireNonNull(config);
        ReloadableRegistry.registerReloadable(this);
        enabled = load(config);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (enabled) {
            event.getPlayer().teleport(location);
        }
    }

    private boolean load(@Nonnull FileConfiguration config) {
        SelectorLogger logger = SelectorLogger.getLogger();

        // Get our section
        if (!(config.contains("Join-Teleporter", true) && config.isConfigurationSection("Join-Teleporter"))) {
            logger.warn("Configuration does not contain Join-Teleporter section, skipping module.");
            return false;
        }
        ConfigurationSection section = config.getConfigurationSection("Join-Teleporter");
        Objects.requireNonNull(section);

        // Validate all our values
        if (!(section.contains("Enable", true) && section.isBoolean(("Enable")))) {
            logger.severe("Join-Teleporter config section does not contain a valid Enable value, skipping module!");
            return false;
        }
        if (!(section.contains("World") && section.isString("World"))) {
            logger.severe("Join-Teleporter config section does not contain a valid World string, skipping module!");
            return false;
        }
        String worldName = section.getString("World");
        Objects.requireNonNull(worldName);
        World world = Bukkit.getServer().getWorld(worldName);
        if (world == null) {
            logger.severe("Join-Teleporter.World in the config is not a valid world, skipping module!");
            return false;
        }

        if (section.contains("Location") && section.isString("Location")) {
            // Make sure the given coordinates are in the correct format
            String composedCoords = section.getString("Location");
            Objects.requireNonNull(composedCoords);
            if (!composedCoords.matches(COORDINATE_REGEX)) {
                logger.severe("Join-Teleporter.Location in the config is not of the format <integer;integer;integer>, skipping module!");
                return false;
            }

            // Decompose the coordinate string into usable values, and set the location to use if successful
            String[] coordinates = composedCoords.split(";", 3);
            try {
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);
                int z = Integer.parseInt(coordinates[2]);
                location = new Location(world, x, y, z);
                return true;
            } catch (NumberFormatException e) {
                throw new AssertionError("Failed to decompose the following coordinates: " + composedCoords + " -> " + Arrays.toString(coordinates));
            }
        } else {
            logger.severe("Join-Teleporter config section does not contain a valid Location value!");
            return false;
        }
    }

    @Override
    public boolean reload() {
        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.MAIN);
        Objects.requireNonNull(config);
        enabled = load(config);
        return enabled;
    }
}
