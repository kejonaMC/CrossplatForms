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
import java.util.Objects;

public class JoinTeleporter implements Listener, Reloadable {

    /**
     * True if the module isn't disabled in the config and all loading was successful.
     */
    private boolean enabled;
    private Location location;

    public JoinTeleporter() {
        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.MAIN);
        ReloadableRegistry.registerReloadable(this);
        enabled = load(config);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (enabled) {
            event.getPlayer().teleport(location);
        }
    }

    /**
     * Load the the JoinTeleporter
     * @param config the config to pull from
     * @return false if there was an error loading or if its disabled in the config
     */
    private boolean load(@Nonnull FileConfiguration config) {
        SelectorLogger logger = SelectorLogger.getLogger();

        if (!config.contains("Join-Teleporter", true) || !config.isConfigurationSection("Join-Teleporter")) {
            logger.warn("Configuration does not contain Join-Teleporter section, skipping module.");
            return false;
        }
        ConfigurationSection section = config.getConfigurationSection("Join-Teleporter");
        Objects.requireNonNull(section);

        if (section.contains("Enable", true) && section.isBoolean("Enable")) {
            if (section.getBoolean("Enable")) {
                return setLocation(section);
            } else {
                return false;
            }
        } else {
            logger.warn("Join-Teleporter config section does not contain a valid Enable value, skipping module!");
            return false;
        }
    }

    /**
     * Set the location to teleport to.
     * @param section the configuration section to pull the data from
     * @return false if there was an error setting the location
     */
    private boolean setLocation(@Nonnull ConfigurationSection section) {
        SelectorLogger logger = SelectorLogger.getLogger();

        if (!(section.contains("World", true) && section.isString("World"))) {
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

        if (section.contains("X") && section.contains("Y") && section.contains("Z") && section.contains("Pitch") && section.contains("Yaw")) {
            if (!(section.isInt("X") && section.isInt("Y") && section.isInt("Z") && section.isInt("Pitch") && section.isInt("Yaw"))) {
                logger.severe("Coordinate and Pitch/Yaw values must all be integers in the Join-Teleporter config section");
                return false;
            }

            int x = section.getInt("X");
            int y = section.getInt("Y");
            int z = section.getInt("Z");
            int pitch = section.getInt("Pitch");
            int yaw = section.getInt("Yaw");
            location = new Location(world, x, y, z, yaw, pitch);
            logger.debug("Join-Teleporter is enabled and has coordinates: [" + x + ", " + y + ", " + z + "] with Pitch and Yaw [" + pitch + ", " + yaw + "] in [" + worldName + "].");

            return true;
        } else {
            logger.severe("Join-Teleporter must have Coordinate and Pitch/Yaw integer values");
            return false;
        }
    }

    @Override
    public boolean reload() {
        enabled = load(GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.MAIN));
        return true;
    }
}
