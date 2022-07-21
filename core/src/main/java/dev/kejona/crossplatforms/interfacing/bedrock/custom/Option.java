package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import dev.kejona.crossplatforms.Resolver;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Option {

    @Nonnull
    @SuppressWarnings("FieldMayBeFinal")
    private String display;

    @Nullable
    private transient final String returnText;

    public Option(@Nonnull String display) {
        this.display = display;
        this.returnText = null;
    }

    public Option(@Nonnull String display, @Nullable String returnText) {
        this.display = display;
        this.returnText = returnText;
    }

    @Contract(pure = true)
    public Option with(Resolver resolver) {
        if (returnText == null) {
            return new Option(resolver.apply(display));
        } else {
            return new Option(resolver.apply(display), resolver.apply(returnText));
        }
    }

    @Nonnull
    public String display() {
        return display;
    }

    @Nonnull
    public String returnText() {
        if (returnText == null) {
            return display;
        } else {
            return returnText;
        }
    }
}
