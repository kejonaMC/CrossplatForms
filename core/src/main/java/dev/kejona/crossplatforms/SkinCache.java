package dev.kejona.crossplatforms;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.utils.SkinUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SkinCache {

    // See https://mc-heads.net/
    private static final String AVATAR_ENDPOINT = "https://mc-heads.net/avatar/";
    // See https://mc-heads.net/minecraft/mhf
    private static final String STEVE = "MHF_Steve";

    private static final Logger LOGGER = Logger.get();

    private final Cache<UUID, String> avatars = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();

    @Nullable
    public String getAvatarUrl(FormPlayer player) {
        UUID uuid = player.getUuid();
        try {
            return avatars.get(uuid, () -> getAvatarUrl(uuid, player.getEncodedSkinData()));
        } catch (ExecutionException e) {
            LOGGER.warn("Exception while computing avatar url of " + player.getName());
            e.printStackTrace();
            return null;
        }
    }

    @Nonnull
    public static String getAvatarUrl(UUID uuid, @Nullable String encodedData) {
        if (encodedData == null) {
            return AVATAR_ENDPOINT + STEVE;
        }
        // todo: calculate default skin if no encoded data or failed to read

        try {
            String avatarUrl = AVATAR_ENDPOINT + SkinUtils.idFromEncoding(encodedData);
            LOGGER.debug("Avatar URL for " + uuid + ": " + avatarUrl);
            return avatarUrl;
        } catch (Exception e) {
            if (LOGGER.isDebug()) {
                LOGGER.debug("Failed to get avatar url for " + uuid);
                e.printStackTrace();
            }
            return AVATAR_ENDPOINT + STEVE;
        }
    }
}
