package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.Resolver;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.Option;
import dev.kejona.crossplatforms.serialize.ValuedType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public interface OptionFiller extends ValuedType {

    default Stream<Option> generateOptions(Resolver resolver) {
        String format = dropdownFormat();
        if (format == null) {
            return rawOptions(resolver).map(Option::new);
        }
        return rawOptions(resolver).map(s -> new Option(FillerUtils.replace(format, s), s));
    }

    @Nonnull
    Stream<String> rawOptions(Resolver resolver);

    @Nullable
    String dropdownFormat();

    int insertIndex();
}
