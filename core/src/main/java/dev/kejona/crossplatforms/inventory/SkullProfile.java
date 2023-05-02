package dev.kejona.crossplatforms.inventory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Getter
public class SkullProfile {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 16;
    private static final String OWNER = "owner";
    private static final String TEXTURES = "textures";

    private static final Gson GSON = new Gson();

    @Nullable
    private final String owner;
    @Nullable
    private final String textures;

    @Contract("null, null -> fail")
    public SkullProfile(@Nullable String owner, @Nullable String textures) {
        if (owner == null && textures == null) {
            throw new IllegalArgumentException("Both owner and textures cannot be null");
        }

        this.owner = owner;
        this.textures = textures;
    }

    public static class Serializer implements TypeSerializer<SkullProfile> {

        @Override
        public SkullProfile deserialize(Type type, ConfigurationNode node) throws SerializationException {
            if (!node.isMap()) {
                throw new SerializationException("Skull must be a config section containing 'owner' or 'textures'");
            }

            String textures = node.node(TEXTURES).getString();
            if (textures != null) {
                // Do this just for validation. Any decoding/parsing exceptions will be wrapped by Configurate.
                String decoded = new String(Base64.getDecoder().decode(textures), StandardCharsets.UTF_8);
                GSON.fromJson(decoded, JsonObject.class).getAsJsonObject("textures");
            }

            String name = node.node(OWNER).getString();
            if (name != null) {
                if (name.length() < MIN_USERNAME_LENGTH || name.length() > MAX_USERNAME_LENGTH) {
                    throw new SerializationException("'owner' is too short or too long to be a valid username");
                }
            }

            if (textures == null && name == null) {
                throw new SerializationException("At least one of 'owner' and 'textures' must be present");
            }

            return new SkullProfile(name, textures);
        }

        @Override
        public void serialize(Type type, @Nullable SkullProfile skull, ConfigurationNode node) throws SerializationException {
            node.raw(null);

            if (skull != null) {
                if (skull.textures != null) {
                    node.node(TEXTURES).set(skull.textures);
                }
                if (skull.owner != null) {
                    node.node(OWNER).set(skull.owner);
                }
            }
        }
    }
}
