package dev.kejona.crossplatforms;

import dev.kejona.crossplatforms.config.ConfigId;
import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import org.bstats.charts.CustomChart;

public interface CrossplatFormsBootstrap {

    /**
     * Perform any operations on the {@link ConfigManager} before {@link ConfigManager#load()} is called. For example, register
     * additional {@link ConfigId}, or register additional type serializers.
     */
    void preConfigLoad(ConfigManager configManager);

    /**
     * Construct an {@link Interfacer} implementation with the given parameters. This method will be called only once
     * during the construction of {@link CrossplatForms}. This method exists
     */
    Interfacer interfaceManager();

    void addCustomChart(CustomChart chart);
}
