package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.Resolver;
import dev.kejona.crossplatforms.interfacing.bedrock.simple.SimpleButton;
import dev.kejona.crossplatforms.serialize.ValuedType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public interface SimpleFormFiller extends ValuedType {

    default Stream<SimpleButton> generateButtons(Resolver resolver) {
        SimpleButton format = buttonFormat();
        if (format == null) {
            return rawButtons(resolver);
        }

        return rawButtons(resolver).map(format::withRaw);
    }

    @Nonnull
    Stream<SimpleButton> rawButtons(Resolver resolver);

    @Nullable
    SimpleButton buttonFormat();

    int insertIndex();
}
