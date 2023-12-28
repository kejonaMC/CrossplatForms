package dev.kejona.crossplatforms.spigot.adapter;

import com.mojang.authlib.properties.Property;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.spigot.SpigotAccessItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SpigotAdapter {

    boolean customModelData();

    void setCustomModelData(@Nonnull ItemStack stack, @Nullable Integer value);

    Material playerHeadMaterial();

    @Contract("_, null, null -> fail")
    void setSkullProfile(SkullMeta meta, String name, String textures);

    default void setSkullProfile(SkullMeta meta, FormPlayer player) {
        setSkullProfile(meta, player.getName(), player.getEncodedSkinData());
    }

    NbtAccessor nbtAccessor(Plugin plugin);

    String propertyValue(Property property);

    void registerAuxiliaryEvents(Plugin plugin, SpigotAccessItems items);
}
