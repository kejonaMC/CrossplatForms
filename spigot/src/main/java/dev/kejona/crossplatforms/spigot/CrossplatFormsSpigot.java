package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.spigot.common.SpigotAccessItemsBase;
import dev.kejona.crossplatforms.spigot.common.SpigotBase;
import org.bstats.charts.SimplePie;

public class CrossplatFormsSpigot extends SpigotBase {

    @Override
    public void onEnable() {
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
