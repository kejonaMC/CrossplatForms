package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.context.PlayerContext;
import dev.kejona.crossplatforms.interfacing.bedrock.simple.SimpleButton;
import dev.kejona.crossplatforms.serialize.ValuedType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public interface SimpleFormFiller extends ValuedType {

    default void fillButtons(List<SimpleButton> container, PlayerContext context) {
        SimpleButton template = buttonTemplate();

        Stream<SimpleButton> options = rawButtons(context);
        if (template != null) {
            options = options.map(template::withRaw);
        }

        FillerUtils.addAtIndex(options, container, insertIndex());
    }

    @Nonnull
    Stream<SimpleButton> rawButtons(PlayerContext context);

    @Nullable
    SimpleButton buttonTemplate();

    int insertIndex();
}
