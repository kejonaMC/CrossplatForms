package dev.kejona.crossplatforms.spigot.handler;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.spigot.ClassNames;
import dev.kejona.crossplatforms.spigot.SpigotBase;
import dev.kejona.crossplatforms.spigot.adapter.SpigotAdapter;
import dev.kejona.crossplatforms.utils.ReflectionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class SpigotPlayer implements FormPlayer {

    private static final SpigotBase PLUGIN = SpigotBase.getInstance();
    private static final SpigotAdapter ADAPTER = PLUGIN.adapter();

    private final Player handle;

    public SpigotPlayer(@Nonnull Player handle) {
        this.handle = Objects.requireNonNull(handle);
    }

    @Override
    public UUID getUuid() {
        return handle.getUniqueId();
    }

    @Override
    public String getName() {
        return handle.getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        if (!handle.getServer().isPrimaryThread()) {
            Logger.get().warn("Permission check off main thread, instead: " + Thread.currentThread());
            Thread.dumpStack();
        }
        return handle.hasPermission(permission);
    }

    @Nullable
    @Override
    public String getEncodedSkinData() {
        GameProfile profile = ReflectionUtils.castedInvoke(handle, ClassNames.PLAYER_GET_PROFILE);
        Objects.requireNonNull(profile, "game profile");

        // Need to be careful here - getProperties() returns an authlib PropertyMap, which extends
        // MultiMap from Guava. On spigot-legacy, Guava is shaded and relocated.
        for (Property textures : profile.getProperties().get("textures")) {
            String value = ADAPTER.propertyValue(textures);
            if (!value.isEmpty()) {
                return value;
            }
        }

        return null;
    }

    @Override
    public void sendRaw(Component component) {
        handle.sendMessage(SpigotBase.LEGACY_SERIALIZER.serialize(component));
    }

    @Override
    public boolean switchBackendServer(String server) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(stream)) {
            out.writeUTF("Connect");
            out.writeUTF(server);
            handle.sendPluginMessage(PLUGIN, "BungeeCord", stream.toByteArray());
        } catch (IOException e) {
            Logger.get().severe("Failed to send a plugin message to BungeeCord!");
            e.printStackTrace();
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getHandle(Class<T> asType) throws ClassCastException {
        return (T) handle;
    }
}
