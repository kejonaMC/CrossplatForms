package dev.projectg.crossplatforms.spigot;

import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.ItemButton;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.spigot.common.SpigotInterfacerBase;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SpigotInterfacer extends SpigotInterfacerBase {

    private static final NamespacedKey BUTTON_KEY = new NamespacedKey(CrossplatFormsSpigot.getInstance(), ItemButton.STATIC_IDENTIFIER);
    private static final PersistentDataType<String, String> BUTTON_KEY_TYPE = PersistentDataType.STRING;

    public SpigotInterfacer(ServerHandler serverHandler, BedrockHandler bedrockHandler, BedrockFormRegistry bedrockRegistry, JavaMenuRegistry javaRegistry) {
        super(serverHandler, bedrockHandler, bedrockRegistry, javaRegistry);
    }

    @Nullable
    @Override
    public String getMenuName(@NotNull ItemMeta meta) {
        return meta.getPersistentDataContainer().get(BUTTON_KEY, BUTTON_KEY_TYPE);
    }

    @Override
    public void setMenuName(@NotNull ItemMeta meta, @NotNull String identifier) {
        Objects.requireNonNull(meta);
        Objects.requireNonNull(identifier);
        meta.getPersistentDataContainer().set(BUTTON_KEY, BUTTON_KEY_TYPE, identifier);
    }
}
