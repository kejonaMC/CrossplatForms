package dev.projectg.crossplatforms.config;

import com.google.inject.AbstractModule;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;

public class ConfigurationModule extends AbstractModule {

    private final InterfaceManager interfaceManager;
    private final BedrockHandler bedrockHandler;
    private final ServerHandler serverHandler;
    private final PlaceholderHandler placeholders;

    public ConfigurationModule(InterfaceManager interfaceManager,
                               BedrockHandler bedrockHandler,
                               ServerHandler serverHandler,
                               PlaceholderHandler placeholders) {
        this.interfaceManager = interfaceManager;
        this.bedrockHandler = bedrockHandler;
        this.serverHandler = serverHandler;
        this.placeholders = placeholders;
    }

    @Override
    protected void configure() {
        bind(InterfaceManager.class).toInstance(interfaceManager);
        bind(BedrockHandler.class).toInstance(bedrockHandler);
        bind(ServerHandler.class).toInstance(serverHandler);
        bind(PlaceholderHandler.class).toInstance(placeholders);
    }
}
