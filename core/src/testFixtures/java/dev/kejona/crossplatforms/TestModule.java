package dev.kejona.crossplatforms;

import com.google.inject.AbstractModule;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.DispatchableCommand;
import dev.kejona.crossplatforms.command.custom.InterceptCommand;
import dev.kejona.crossplatforms.handler.BasicPlaceholders;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;
import dev.kejona.crossplatforms.permission.PermissionDefault;
import dev.kejona.crossplatforms.resolver.Resolver;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Interfacer.class).toInstance(new Interfacer() {
            @Override
            public void sendMenu(FormPlayer player, JavaMenu menu, @Nonnull Resolver resolver) {

            }
        });

        bind(BedrockHandler.class).toInstance(BedrockHandler.empty());

        bind(ServerHandler.class).toInstance(new ServerHandler() {
            @Nullable
            @Override
            public FormPlayer getPlayer(UUID uuid) {
                return null;
            }

            @Nullable
            @Override
            public FormPlayer getPlayer(String name) {
                return null;
            }

            @Override
            public Stream<FormPlayer> getPlayers() {
                return null;
            }

            @Nonnull
            @Override
            public Audience asAudience(CommandOrigin origin) {
                return Audience.empty();
            }

            @Override
            public boolean isGeyserEnabled() {
                return false;
            }

            @Override
            public boolean isFloodgateEnabled() {
                return false;
            }

            @Override
            public void registerPermission(String key, @Nullable String description, PermissionDefault def) {

            }

            @Override
            public void unregisterPermission(String key) {

            }

            @Override
            public void dispatchCommand(DispatchableCommand command) {

            }

            @Override
            public void dispatchCommand(UUID player, DispatchableCommand command) {

            }

            @Override
            public void registerInterceptCommand(InterceptCommand proxyCommand) {

            }

            @Override
            public void clearInterceptCommands() {

            }
        });

        bind(Placeholders.class).toInstance(new BasicPlaceholders());
    }
}
