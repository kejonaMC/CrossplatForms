package dev.kejona.crossplatforms.action;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ConfigSerializable
public class MessageAction implements Action {

    public static final String TYPE = "message";
    private static final GsonComponentSerializer DOWNSAMPLING_GSON_SERIALIZER = GsonComponentSerializer.colorDownsamplingGson();
    private static final GsonComponentSerializer GSON_SERIALIZER = GsonComponentSerializer.gson();

    private Format format = Format.LEGACY;
    private char character = LegacyComponentSerializer.SECTION_CHAR;
    private String message = null;
    private List<String> messages = null;

    private transient final Placeholders placeholders;
    private transient Function<String, Component> deserializer;

    @Inject
    private MessageAction(Placeholders placeholders) {
        this.placeholders = placeholders;
    }

    @Override
    public void affectPlayer(@NotNull FormPlayer player, @NotNull Map<String, String> additionalPlaceholders) {
        if (message != null) {
            player.sendRaw(deserializer.apply(placeholders.setPlaceholders(player, message, additionalPlaceholders)));
        }
        if (messages != null) {
            messages.stream()
                .map(s -> placeholders.setPlaceholders(player, s, additionalPlaceholders))
                .map(deserializer)
                .forEachOrdered(player::sendRaw);
        }
    }

    @Override
    public String type() {
        return TYPE;
    }

    enum Format {
        LEGACY,
        JSON,
        JSON_OLD

        // todo: support minimessage
    }

    // Determine which serializer to use
    @PostProcess
    private void postProcess() {
        if (format == Format.LEGACY) {
            final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
                .character(character)
                .extractUrls() // turn URLs into click events
                .hexColors() // support hex colours (Using # char)
                .build();

            deserializer = serializer::deserialize;
        } else if (format == Format.JSON) {
            deserializer = GSON_SERIALIZER::deserialize;
        } else if (format == Format.JSON_OLD) {
            deserializer = DOWNSAMPLING_GSON_SERIALIZER::deserialize;
        } else {
            throw new AssertionError();
        }
    }
}
