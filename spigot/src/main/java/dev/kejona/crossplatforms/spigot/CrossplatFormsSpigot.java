package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.spigot.SpigotBase;
import dev.kejona.crossplatforms.spigot.adapter.VersionAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public class CrossplatFormsSpigot extends SpigotBase {

    @Override
    public void onEnable() {

    }

    @Override
    public VersionAdapter createVersionAdapter(JavaPlugin plugin) {
        return null;
    }
}
