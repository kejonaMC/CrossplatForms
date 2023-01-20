package dev.kejona.crossplatforms.spigot.v1_8_R3;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.spigot.ClassNames;
import dev.kejona.crossplatforms.spigot.SpigotAccessItems;
import dev.kejona.crossplatforms.spigot.adapter.NbtAccessor;
import dev.kejona.crossplatforms.spigot.adapter.VersionAdapter;
import dev.kejona.crossplatforms.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Adapter_v1_8_R3 implements VersionAdapter {

    @Override
    public boolean customModelData() {
        return false;
    }

    @Override
    public void setCustomModelData(@Nonnull ItemStack stack, @Nullable Integer value) {
        throw new UnsupportedOperationException("CustomModelData only supported on 1.14.4 and above. Current version is " + Bukkit.getVersion());
    }

    @Override
    public Material playerHeadMaterial() {
        return Material.SKULL_ITEM;
    }

    @Override
    public void setSkullProfile(SkullMeta meta, FormPlayer player) {
        GameProfile profile = new GameProfile(player.getUuid(), player.getName());
        profile.getProperties().put("textures", new Property("textures", player.getEncodedSkinData()));

        ReflectionUtils.setValue(meta, ClassNames.META_SKULL_PROFILE, profile);
    }

    @Override
    public NbtAccessor nbtAccessor(Plugin plugin) {
        return new LegacyNbtAccessor(plugin);
    }

    @Override
    public void registerAuxiliaryEvents(Plugin plugin, SpigotAccessItems items) {
        PlayerPickupItemListener listener = new PlayerPickupItemListener(items);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
