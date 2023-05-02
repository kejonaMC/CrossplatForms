package dev.kejona.crossplatforms.spigot.v1_8_R3;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.kejona.crossplatforms.spigot.ClassNames;
import dev.kejona.crossplatforms.spigot.SpigotAccessItems;
import dev.kejona.crossplatforms.spigot.adapter.NbtAccessor;
import dev.kejona.crossplatforms.spigot.adapter.SpigotAdapter;
import dev.kejona.crossplatforms.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class Adapter_v1_8_R3 implements SpigotAdapter {

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
    public void setSkullProfile(SkullMeta meta, @Nullable UUID id, @Nullable String name, @Nullable String textures) {
        if (id == null && textures == null) {
            meta.setOwner(name); // only name provided
            return;
        }

        if (id == null && name == null) {
            id = UUID.randomUUID(); // GameProfile cannot have both name and ID blank
        }

        GameProfile profile = new GameProfile(id, name);

        if (textures != null) {
            profile.getProperties().put("textures", new Property("textures", textures));
        }

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
