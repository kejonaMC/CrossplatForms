package dev.kejona.crossplatforms.filler;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.ServerHandler;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collection;
import java.util.stream.Collectors;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class PlayerFiller extends Filler {

    public static final String TYPE = "player";

    @Inject
    private transient ServerHandler serverHandler = null;

    @Override
    public Collection<String> generate() {
        return serverHandler.getPlayers().map(FormPlayer::getName).sorted().collect(Collectors.toList());
    }

    @Override
    public String type() {
        return TYPE;
    }
}
