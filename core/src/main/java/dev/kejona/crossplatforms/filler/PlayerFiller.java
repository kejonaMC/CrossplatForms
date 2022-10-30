package dev.kejona.crossplatforms.filler;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.SkinCache;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.bedrock.simple.SimpleButton;
import dev.kejona.crossplatforms.interfacing.java.ItemButton;
import dev.kejona.crossplatforms.resolver.Resolver;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

@ConfigSerializable
public class PlayerFiller extends UniversalFiller {

    private static final String TYPE = "player";

    private final transient ServerHandler serverHandler;
    private final transient SkinCache skinCache;

    @Inject
    private PlayerFiller(ServerHandler serverHandler, SkinCache skinCache) {
        this.serverHandler = serverHandler;
        this.skinCache = skinCache;
    }

    @Nonnull
    @Override
    public Stream<String> rawOptions(Resolver resolver) {
        return serverHandler.getPlayerNames().sorted();
    }

    @Nonnull
    @Override
    public Stream<SimpleButton> rawButtons(Resolver resolver) {
        return serverHandler.getPlayersSorted().map(player -> new SimpleButton(player.getName(), headLink(player)));
    }

    @Nonnull
    @Override
    public Stream<ItemButton> rawItems(Resolver resolver) {
        return serverHandler.getPlayersSorted().map(player -> ItemButton.fillEntry(player.getName(), player));
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Nullable
    private String headLink(FormPlayer player) {
        return skinCache.getAvatarUrl(player);
    }

    public static void register(FillerSerializer serializer) {
        serializer.filler(TYPE, PlayerFiller.class);
    }
}
