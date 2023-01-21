package dev.kejona.crossplatforms.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SkinUtils {

    private static final Gson GSON = new Gson();
    private static final Pattern URL_PATTERN = Pattern.compile("(http|https)://textures\\.minecraft\\.net/texture/([a-zA-Z0-9]+)");

    private SkinUtils() {

    }

    @Nonnull
    public static String readSkinUrl(@Nonnull String encodedData) throws IllegalArgumentException {

        // See https://wiki.vg/Mojang_API#UUID_to_Profile_and_Skin.2FCape
        JsonObject json;
        try {
            String decoded = new String(Base64.getDecoder().decode(encodedData), StandardCharsets.UTF_8);
            json = GSON.fromJson(decoded, JsonObject.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decode base64 textures value: " + encodedData, e);
        }

        JsonObject textures = json.getAsJsonObject("textures");
        if (textures == null) {
            throw new IllegalArgumentException("textures member missing in " + encodedData);
        }

        JsonObject skin = textures.getAsJsonObject("SKIN");
        if (skin == null) {
            throw new IllegalArgumentException("SKIN member missing in " + encodedData);
        }

        JsonElement url = skin.get("url");
        if (url == null) {
            throw new IllegalArgumentException("url member missing in " + encodedData);
        }

        try {
            return Objects.requireNonNull(url.getAsString(), "url as string");
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get url element in " + encodedData, e);
        }
    }

    public static boolean isValidUrl(String skinUrl) {
        return URL_PATTERN.matcher(skinUrl).matches();
    }

    public static void requireValidUrl(String skinUrl) {
        if (!isValidUrl(skinUrl)) {
            throw new IllegalArgumentException("Skin url has unexpected format: " + skinUrl);
        }
    }

    @Nonnull
    public static String getSkinId(String skinUrl) {
        Matcher matcher = URL_PATTERN.matcher(skinUrl);
        if (matcher.matches()) {
            return matcher.group(2);
        } else {
            throw new IllegalArgumentException("Skin url has unexpected format: " + skinUrl);
        }
    }
}
