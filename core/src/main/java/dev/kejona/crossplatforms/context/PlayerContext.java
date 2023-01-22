package dev.kejona.crossplatforms.context;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.resolver.Resolver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class PlayerContext implements Context {

    private final FormPlayer player;
    private final Resolver resolver;
}
