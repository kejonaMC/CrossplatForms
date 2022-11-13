package dev.kejona.crossplatforms;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.kejona.crossplatforms.handler.FormPlayer;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkinCache {

    private static final Gson GSON = new Gson();

    // See https://mc-heads.net/
    private static final String AVATAR_ENDPOINT = "https://mc-heads.net/avatar/";
    // See https://mc-heads.net/minecraft/mhf
    private static final String STEVE = "MHF_Steve";
    private static final String ALEX = "MHF_Alex";

    private static final Pattern URL_PATTERN = Pattern.compile("(http|https)://textures\\.minecraft\\.net/texture/([a-zA-Z0-9]+)");
    private static final Logger LOGGER = Logger.get();

    private final Cache<UUID, String> avatars = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();

    @Nullable
    public String getAvatarUrl(FormPlayer player) {
        UUID uuid = player.getUuid();
        try {
            return avatars.get(uuid, () -> readAvatarUrl(uuid, player.getEncodedSkinData()));
        } catch (ExecutionException e) {
            LOGGER.warn("Exception while computing avatar url of " + player.getName());
            e.printStackTrace();
            return null;
        }
    }

    public static String readAvatarUrl(UUID uuid, @Nullable String encodedData) {
        String url = AVATAR_ENDPOINT + readSkinId(uuid, encodedData);
        LOGGER.debug("Avatar URL for " + uuid + ": " + url);
        return url;
    }

    public static String readSkinId(UUID uuid, @Nullable String encodedData) {
        if (encodedData == null) {
            LOGGER.debug("textures property (encoded) missing or empty for " + uuid + ", falling back to steve");
            return STEVE;
        }

        // See https://wiki.vg/Mojang_API#UUID_to_Profile_and_Skin.2FCape
        String decoded = new String(Base64.getDecoder().decode(encodedData), StandardCharsets.UTF_8);
        JsonObject textures = GSON.fromJson(decoded, JsonObject.class).getAsJsonObject("textures");
        if (textures == null) {
            LOGGER.debug("textures member missing for " + uuid + ", falling back to steve");
            return STEVE;
        }

        JsonObject skin = textures.getAsJsonObject("SKIN");
        if (skin == null) {
            LOGGER.debug(uuid + " does not have custom skin, using steve or alex");
            // no custom skin
            if ((uuid.hashCode() & 1) == 0) {
                return STEVE; // even hashcode
            } else {
                return ALEX; // odd hashcode
            }
        }

        String url = skin.get("url").getAsString();
        Matcher matcher = URL_PATTERN.matcher(url);
        if (matcher.matches()) {
            return matcher.group(2);
        } else {
            LOGGER.debug("Skin url of " + uuid + " has unexpected format: " + url);
            return STEVE;
        }
    }
}
