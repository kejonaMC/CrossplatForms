package dev.kejona.crossplatforms.filler;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.bedrock.simple.SimpleButton;
import dev.kejona.crossplatforms.interfacing.java.ItemButton;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

@ConfigSerializable
public class PlayerFiller extends UniversalFiller {

    private static final String TYPE = "player";

    private final transient ServerHandler serverHandler;

    @Inject
    private PlayerFiller(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
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

    private static String headLink(FormPlayer player) {
        return "https://api.tydiumcraft.net/v1/players/skin?uuid=" + player.getUuid() + "&type=avatar";
    }

    public static void register(FillerSerializer serializer) {
        serializer.filler(TYPE, PlayerFiller.class);
    }
}
