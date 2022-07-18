package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import dev.kejona.crossplatforms.Resolver;
import dev.kejona.crossplatforms.filler.OptionFiller;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;

/**
 * Entry of a dropdown, from a {@link OptionFiller}
 */
@AllArgsConstructor
@Accessors(fluent = true)
@Getter
public class FilledOption implements Option {

    @Nonnull
    private final String display;

    @Nonnull
    private final String returnText;

    public FilledOption(@Nonnull String singleton) {
        display = singleton;
        returnText = singleton;
    }

    @Override
    public Option with(Resolver resolver) {
        return new FilledOption(resolver.apply(display), resolver.apply(returnText));
    }
}
