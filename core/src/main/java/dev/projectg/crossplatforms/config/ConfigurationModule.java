package dev.projectg.crossplatforms.config;

import com.google.inject.AbstractModule;
import com.google.inject.util.Providers;
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
        // Hack to stop the instance from having its members being injected
        // which causes a ClassDefNotFound error if Cumulus is not present (EmptyBedrockHandler)
        bind(BedrockHandler.class).toProvider(Providers.of(bedrockHandler));
        bind(ServerHandler.class).toInstance(serverHandler);
        bind(PlaceholderHandler.class).toInstance(placeholders);
    }
}
