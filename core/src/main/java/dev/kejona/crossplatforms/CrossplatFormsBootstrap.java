package dev.kejona.crossplatforms;

import com.google.inject.Module;
import dev.kejona.crossplatforms.config.ConfigId;
import dev.kejona.crossplatforms.config.ConfigManager;
import org.bstats.charts.CustomChart;
import org.jetbrains.annotations.Contract;

import java.util.List;

public interface CrossplatFormsBootstrap {

    /**
     * Returns A List of modules that should be used for injection of configuration instances. It is expected that this
     * List can be added to without any consequences. It is not expected that these modules provide bindings for interfaces
     * and classes already provided in the {@link CrossplatForms} constructor.
     *
     * @return a List of modules as described
     */
    @Contract(" -> new")
    List<Module> configModules();

    /**
     * Perform any operations on the {@link ConfigManager} before {@link ConfigManager#load()} is called. For example, register
     * additional {@link ConfigId}, or register additional type serializers.
     */
    void preConfigLoad(ConfigManager configManager);

    void addCustomChart(CustomChart chart);
}
