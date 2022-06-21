package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.serialize.ValuedTypeSerializer;

public class FillerSerializer extends ValuedTypeSerializer<Filler> {

    public FillerSerializer() {
        registerType(PlayerFiller.TYPE, PlayerFiller.class);
    }
}
