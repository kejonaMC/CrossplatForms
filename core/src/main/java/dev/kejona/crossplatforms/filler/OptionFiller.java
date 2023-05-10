package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.context.PlayerContext;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.Option;
import dev.kejona.crossplatforms.serialize.KeyedType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface OptionFiller extends KeyedType {

    Pattern PLACEHOLDER = Pattern.compile("%raw_text%");

    default void fillOptions(List<Option> container, PlayerContext context) {
        Option template = optionTemplate();

        Stream<Option> options;
        if (template == null) {
            options = rawOptions(context).map(Option::new);
        } else {
            // Note: it is key to set the returnText of the Option so that it is the raw value without formatting
            options = rawOptions(context).map(s -> new Option(FillerUtils.replace(template.display(), PLACEHOLDER, s), s));
        }

        FillerUtils.addAtIndex(options, container, insertIndex());
    }

    @Nonnull
    Stream<String> rawOptions(PlayerContext context);

    @Nullable
    Option optionTemplate();

    int insertIndex();
}
