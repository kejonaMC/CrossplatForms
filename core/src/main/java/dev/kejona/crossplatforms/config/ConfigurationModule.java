package dev.kejona.crossplatforms.config;

import com.google.inject.AbstractModule;
import com.google.inject.util.Providers;
import dev.kejona.crossplatforms.SkinCache;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConfigurationModule extends AbstractModule {

    private final Interfacer interfacer;
    private final BedrockHandler bedrockHandler;
    private final ServerHandler serverHandler;
    private final Placeholders placeholders;

    @Override
    protected void configure() {
        // remember: explicit bindings cannot be required because this module is used for creating configs

        bind(Interfacer.class).toInstance(interfacer);
        // Hack to stop the instance from having its members being injected
        // which causes a ClassDefNotFound error if Cumulus is not present (EmptyBedrockHandler)
        bind(BedrockHandler.class).toProvider(Providers.of(bedrockHandler));
        bind(ServerHandler.class).toInstance(serverHandler);
        bind(Placeholders.class).toInstance(placeholders);
        bind(SkinCache.class).asEagerSingleton();
    }
}
