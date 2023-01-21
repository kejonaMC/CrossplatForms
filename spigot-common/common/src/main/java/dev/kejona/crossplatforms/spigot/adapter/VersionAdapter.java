package dev.kejona.crossplatforms.spigot.adapter;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.spigot.SpigotAccessItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface VersionAdapter {

    boolean customModelData();

    void setCustomModelData(@Nonnull ItemStack stack, @Nullable Integer value);

    Material playerHeadMaterial();

    @Contract("_, null, null, null -> fail")
    void setSkullProfile(SkullMeta meta, UUID id, String name, String textures);

    default void setSkullProfile(SkullMeta meta, FormPlayer player) {
        setSkullProfile(meta, player.getUuid(), player.getName(), player.getEncodedSkinData());
    }

    NbtAccessor nbtAccessor(Plugin plugin);

    void registerAuxiliaryEvents(Plugin plugin, SpigotAccessItems items);
}
