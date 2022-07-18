package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import dev.kejona.crossplatforms.Resolver;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

/**
 * Entry of a dropdown, literally just a string. Constructed using a configurate serializer.
 */
@AllArgsConstructor
public class ConfigurateOption implements Option {

    @Nonnull
    private final String text;

    @Override
    public String display() {
        return text;
    }

    @Override
    public String returnText() {
        return text;
    }

    @Override
    public Option with(Resolver resolver) {
        return new ConfigurateOption(resolver.apply(text));
    }
}
