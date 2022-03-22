package dev.projectg.crossplatforms;

import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import org.bstats.charts.CustomChart;

public interface CrossplatFormsBoostrap {

    /**
     * Perform any operations on the {@link ConfigManager} before {@link ConfigManager#load()} is called. For example, register
     * additional {@link ConfigId}, or register additional type serializers.
     */
    void preConfigLoad(ConfigManager configManager);

    /**
     * Construct an {@link InterfaceManager} implementation with the given parameters. This method will be called only once
     * during the construction of {@link CrossplatForms}. This method exists
     */
    InterfaceManager interfaceManager(BedrockHandler bedrockHandler, BedrockFormRegistry bedrockRegistry, JavaMenuRegistry menuRegistry);

    void addCustomChart(CustomChart chart);
}
