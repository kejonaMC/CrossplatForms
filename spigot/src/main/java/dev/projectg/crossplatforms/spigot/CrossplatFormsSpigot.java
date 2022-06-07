package dev.projectg.crossplatforms.spigot;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.spigot.common.SpigotAccessItemsBase;
import dev.projectg.crossplatforms.spigot.common.SpigotBase;
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
    public SpigotAccessItemsBase accessItems(CrossplatForms crossplatForms) {
        return new SpigotAccessItems(
            this,
            crossplatForms.getConfigManager(),
            crossplatForms.getServerHandler(),
            crossplatForms.getInterfacer(),
            crossplatForms.getBedrockHandler(),
            crossplatForms.getPlaceholders()
        );
    }
}
