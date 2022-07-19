package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import dev.kejona.crossplatforms.Resolver;
import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ConfigSerializable
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

    public Option(@Nonnull String display, @Nonnull String returnText) {
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

    public String display() {
        return display;
    }

    public String returnText() {
        if (returnText == null) {
            return display;
        } else {
            return returnText;
        }
    }
}
