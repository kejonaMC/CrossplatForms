package dev.projectg.crossplatforms.spigot;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.spigot.common.SpigotAccessItemsBase;
import dev.projectg.crossplatforms.spigot.common.SpigotBase;
import org.bstats.charts.SimplePie;

public class CrossplatFormsSpigotLegacy extends SpigotBase {

    @Override
    public void onEnable() {
        super.onEnable();
        addCustomChart(new SimplePie(PIE_CHART_LEGACY, () -> "true")); // this is legacy
    }

    @Override
    public boolean attemptBrigadier() {
        return server.getVersion().contains("1.13"); // Spigot-Legacy supports 1.8 -> 1.13 and Brigadier was introduced in 1.13
    }

    @Override
    public SpigotAccessItemsBase accessItems(CrossplatForms crossplatForms) {
        return new LegacySpigotAccessItems(
            this,
            crossplatForms.getConfigManager(),
            crossplatForms.getServerHandler(),
            crossplatForms.getInterfacer(),
            crossplatForms.getBedrockHandler(),
            crossplatForms.getPlaceholders()
        );
    }
}
