package dev.projectg.crossplatforms.spigot;

import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.spigot.common.SpigotInterfacerBase;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacySpigotInterfacer extends SpigotInterfacerBase {

    public LegacySpigotInterfacer(ServerHandler serverHandler, BedrockHandler bedrockHandler, BedrockFormRegistry bedrockRegistry, JavaMenuRegistry javaRegistry) {
        super(serverHandler, bedrockHandler, bedrockRegistry, javaRegistry);
    }

    @Nullable
    @Override
    public String getMenuName(@NotNull ItemMeta meta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMenuName(@NotNull ItemMeta meta, @NotNull String identifier) {
        throw new UnsupportedOperationException();
    }
}
