package dev.projectg.crossplatforms;

import com.google.inject.AbstractModule;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.command.custom.InterceptCommand;
import dev.projectg.crossplatforms.handler.BasicPlaceholders;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.Placeholders;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.Interfacer;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Interfacer.class).toInstance(new Interfacer() {
            @Override
            public void sendMenu(FormPlayer player, JavaMenu menu) {

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
            public List<FormPlayer> getPlayers() {
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
