package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.spigot.common.SpigotAccessItemsBase;
import dev.kejona.crossplatforms.spigot.common.SpigotBase;
import org.bstats.charts.SimplePie;

public class CrossplatFormsSpigot extends SpigotBase {

    @Override
    public void onEnable() {
        try {
            // Only available on 1.14 and above, which CrossplatForms-Spigot targets. SpigotLegacy is for less than 1.13
            Class.forName("org.bukkit.persistence.PersistentDataContainer");
        } catch (ClassNotFoundException e) {
            getLogger().severe("CrossplatForms-SpigotLegacy must be used for 1.13 and below.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        super.onEnable();
        addCustomChart(new SimplePie(PIE_CHART_LEGACY, () -> "false")); // not legacy
    }

    @Override
    public boolean attemptBrigadier() {
        return true;
    }

    @Override
    public SpigotAccessItemsBase createAccessItems(CrossplatForms crossplatForms) {
        return new SpigotAccessItems(
            this,
            crossplatForms.getConfigManager(),
            crossplatForms.getPermissions(),
            crossplatForms.getInterfacer(),
            crossplatForms.getBedrockHandler(),
            crossplatForms.getPlaceholders()
        );
    }
}
