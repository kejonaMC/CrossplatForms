package dev.kejona.crossplatforms.filler;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Resolver;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.bedrock.simple.SimpleButton;
import dev.kejona.crossplatforms.interfacing.java.ItemButton;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class PlayerFiller extends UniversalFiller {

    public static final String TYPE = "player";

    @Inject
    private transient ServerHandler serverHandler = null;

    @Inject
    private PlayerFiller() {

    }

    @Nonnull
    @Override
    public Stream<String> rawOptions(Resolver resolver) {
        return serverHandler.getPlayers().map(FormPlayer::getName).sorted();
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
}
