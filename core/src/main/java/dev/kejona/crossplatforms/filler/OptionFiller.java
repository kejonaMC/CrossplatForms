package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.Option;
import dev.kejona.crossplatforms.serialize.ValuedType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface OptionFiller extends ValuedType {

    Pattern PLACEHOLDER = Pattern.compile("%raw_text%");

    default Stream<Option> generateOptions(Resolver resolver) {
        Option optionFormat = optionFormat();
        if (optionFormat == null) {
            return rawOptions(resolver).map(Option::new);
        }
        String format = optionFormat.display();
        return rawOptions(resolver).map(s -> new Option(FillerUtils.replace(format, PLACEHOLDER, s), s));
    }

    @Nonnull
    Stream<String> rawOptions(Resolver resolver);

    @Nullable
    Option optionFormat();

    int insertIndex();
}
