package dev.projectg.geyserhub.config.updaters;

import dev.projectg.geyserhub.config.ConfigUpdater;
import org.bukkit.configuration.ConfigurationSection;

public class MAIN_4 implements ConfigUpdater {

    @Override
    public boolean update(ConfigurationSection config) throws IllegalArgumentException {

        config.set("Bungeecord-Message", "§fTeleporting to §b%server%");
        config.set("Config-Version", 5);

        return true;
    }
}
