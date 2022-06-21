package dev.kejona.crossplatforms.filler;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.ServerHandler;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.stream.Stream;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class PlayerFiller extends Filler {

    public static final String TYPE = "PLAYER";

    @Inject
    private transient ServerHandler serverHandler = null;

    @Override
    protected Stream<String> generateRaw() {
        return serverHandler.getPlayers()
            .stream()
            .map(FormPlayer::getName)
            .sorted();
    }

    @Override
    public String type() {
        return TYPE;
    }
}
