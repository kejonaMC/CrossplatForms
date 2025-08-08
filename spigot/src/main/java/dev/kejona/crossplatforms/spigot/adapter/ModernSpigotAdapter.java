package dev.kejona.crossplatforms.spigot.adapter;

import com.mojang.authlib.properties.Property;
import dev.kejona.crossplatforms.spigot.pdc.ModernPDCAccessor;
import dev.kejona.crossplatforms.spigot.pdc.PDCAccessor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ModernSpigotAdapter implements SpigotAdapter {

    @Override
    public boolean customModelData() {
        return true;
    }

    @Override
    public void setCustomModelData(@Nonnull ItemStack stack, @Nullable Integer value) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(value);
            stack.setItemMeta(meta);
        }
    }

    @Override
    public Material playerHeadMaterial() {
        return Material.PLAYER_HEAD;
    }

    @Override
    public void setSkullProfile(SkullMeta meta, @Nullable String name, @Nullable String textures) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }

        if (textures == null) {
            meta.setOwner(name);
            return;
        }

        try {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), name);
            PlayerTextures playerTextures = profile.getTextures();

            String textureUrl = decodeTextureUrl(textures);
            if (textureUrl != null) {
                playerTextures.setSkin(new URL(textureUrl));
                profile.setTextures(playerTextures);
                meta.setOwnerProfile(profile);
            } else {
                meta.setOwner(name);
            }
        } catch (MalformedURLException e) {
            meta.setOwner(name);
        }
    }

    private String decodeTextureUrl(String encodedTextures) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encodedTextures);
            String json = new String(decoded);

            JsonObject jsonObj = JsonParser.parseString(json).getAsJsonObject();
            JsonObject textures = jsonObj.getAsJsonObject("textures");
            JsonObject skin = textures.getAsJsonObject("SKIN");
            return skin.get("url").getAsString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public PDCAccessor pdcAccessor(Plugin plugin) {
        return new ModernPDCAccessor(plugin);
    }

    @Override
    public String propertyValue(Property property) {
        return property.getValue();
    }
}