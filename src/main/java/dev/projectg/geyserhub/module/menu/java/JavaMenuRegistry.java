package dev.projectg.geyserhub.module.menu.java;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.config.ConfigId;
import dev.projectg.geyserhub.reloadable.Reloadable;
import dev.projectg.geyserhub.reloadable.ReloadableRegistry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import javax.annotation.Nonnull;
import java.util.*;

public class JavaMenuRegistry implements Reloadable {

    public static final String DEFAULT = "default";

    /**
     * If bedrock forms are enabled. may be false if disabled in the config or if all forms failed to load.
     */
    private boolean isEnabled;
    private final Map<String, JavaForm> enabledMenus = new HashMap<>();

    public JavaMenuRegistry() {
        ReloadableRegistry.registerReloadable(this);
        isEnabled = load();
    }

    private boolean load() {
        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.SELECTOR);
        SelectorLogger logger = SelectorLogger.getLogger();

        enabledMenus.clear();

        //

        return false;
    }

    public void sendForm(@Nonnull Player player, @Nonnull String form) {
        enabledMenus.get(form);
        // todo: fill
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
