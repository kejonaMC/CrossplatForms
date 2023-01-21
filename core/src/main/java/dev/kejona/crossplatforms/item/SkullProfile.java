package dev.kejona.crossplatforms.item;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Getter
@ConfigSerializable
public class SkullProfile {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 16;
    private static final String OWNER = "owner";
    private static final String TEXTURES = "textures";

    private static final Gson GSON = new Gson();

    @Nullable
    private final String ownerName;
    @Nullable
    private final UUID ownerId;
    @Nullable
    private final String texturesValue;

    @Contract("null, null, null -> fail")
    public SkullProfile(String ownerName, UUID ownerId, String texturesValue) {
        if (ownerName == null && ownerId == null && texturesValue == null) {
            throw new IllegalArgumentException("At least one of ownerName, ownerId, and texturesValue must be non null");
        }

        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.texturesValue = texturesValue;
    }

    public static class Serializer implements TypeSerializer<SkullProfile> {

        @Override
        public SkullProfile deserialize(Type type, ConfigurationNode node) throws SerializationException {
            if (!node.isMap()) {
                throw new SerializationException("Skull must be a config section containing 'owner' or 'textures'");
            }

            String textures = node.node(TEXTURES).getString();
            if (textures != null) {
                // Do this just to make sure it is valid
                String decoded = new String(Base64.getDecoder().decode(textures), StandardCharsets.UTF_8);
                GSON.fromJson(decoded, JsonObject.class).getAsJsonObject("textures");

                return new SkullProfile(null, null, textures);
            }

            try {
                UUID uuid = node.node(OWNER).get(UUID.class);
                return new SkullProfile(null, uuid, null);
            } catch (Exception ignored) {
                String name = node.node(OWNER).getString();
                if (name == null || name.isEmpty()) {
                    throw new SerializationException("'owner' is empty or not present");
                }

                if (name.length() < MIN_USERNAME_LENGTH || name.length() > MAX_USERNAME_LENGTH) {
                    throw new SerializationException("'owner' is not a valid UUID and the length is too short or too long to be a valid username");
                }

                return new SkullProfile(name, null, null);
            }
        }

        @Override
        public void serialize(Type type, @Nullable SkullProfile skull, ConfigurationNode node) throws SerializationException {
            if (skull != null) {
                if (skull.texturesValue != null) {
                    node.node(TEXTURES).set(skull.texturesValue);
                } else if (skull.ownerId != null) {
                    node.node(OWNER).set(skull.ownerId);
                } else if (skull.ownerName != null) {
                    node.node(OWNER).set(skull.ownerName);
                }
            }
        }
    }
}
