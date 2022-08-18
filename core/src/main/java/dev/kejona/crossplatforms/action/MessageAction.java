package dev.kejona.crossplatforms.action;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.serialize.TypeResolver;
import dev.kejona.crossplatforms.utils.ConfigurateUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.serialize.SerializationException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class MessageAction implements Action<Object> {

    private static final String TYPE = "message";

    private transient Function<String, Component> deserializer;

    private Format format = Format.LEGACY;
    private char character = LegacyComponentSerializer.SECTION_CHAR;
    private String message = null;
    private List<String> messages = null;

    @Inject
    private MessageAction() {

    }

    @Override
    public void affectPlayer(@NotNull FormPlayer player, @NotNull Resolver resolver, @Nonnull Object source) {
        if (message != null) {
            player.sendRaw(deserializer.apply(resolver.apply(message)));
        }
        if (messages != null) {
            messages.stream()
                .map(resolver::apply)
                .map(deserializer)
                .forEachOrdered(player::sendRaw);
        }
    }

    @Override
    public String type() {
        return TYPE;
    }

    // Determine which serializer to use
    @PostProcess
    private void postProcess() throws SerializationException {
        if (message == null && messages == null) {
            throw new SerializationException("'messages' is not present.");
        }

        if (format == Format.LEGACY) {
            final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
                    .character(character)
                    .extractUrls() // turn URLs into click events
                    .hexColors() // support hex colours (Using # char)
                    .build();

            deserializer = serializer::deserialize;
        } else if (format == Format.JSON) {
            final GsonComponentSerializer serializer = GsonComponentSerializer.gson();
            deserializer = serializer::deserialize;
        } else {
            throw new AssertionError();
        }
    }

    @Override
    public boolean serializeWithType() {
        return false; // can infer based on message or messages node (at least one must be present)
    }

    public static void register(ActionSerializer serializer) {
        serializer.register(TYPE, MessageAction.class, typeResolver());
    }

    private static TypeResolver typeResolver() {
        return node -> {
            if (node.node("message").getString() != null || ConfigurateUtils.isListOrScalar(node, "messages")) {
                return TYPE;
            } else {
                return null;
            }
        };
    }

    enum Format {
        LEGACY,
        JSON

        // todo: support minimessage
    }
}
