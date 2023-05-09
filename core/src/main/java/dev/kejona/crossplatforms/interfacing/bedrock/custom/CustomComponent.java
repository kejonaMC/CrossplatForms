package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import dev.kejona.crossplatforms.IllegalValueException;
import dev.kejona.crossplatforms.context.PlayerContext;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.OptionalElement;
import dev.kejona.crossplatforms.parser.Parser;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.serialize.KeyedType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ToString(callSuper = true)
public abstract class CustomComponent extends OptionalElement implements KeyedType {

    @Getter
    protected String text = "";

    @Getter
    @Setter
    private List<Parser> parsers = new ArrayList<>(0);

    /**
     * Implementing classes should provide a zero arg constructor that calls super the constructor below
     */
    @SuppressWarnings("unused")
    protected CustomComponent() {
        //no-op
    }

    protected CustomComponent(@Nonnull String text) {
        this.text = Objects.requireNonNull(text);
    }

    public void parser(Parser parser) {
        parsers.add(parser);
    }

    public abstract CustomComponent copy();

    public abstract CustomComponent preparedCopy(PlayerContext context);

    public abstract Component cumulusComponent() throws IllegalValueException;

    /**
     * Copies data in a source {@link CustomComponent} or any of its parent classes into this Component.
     */
    protected final void copyBasics(CustomComponent source) {
        this.text = source.text;
        this.parsers = new ArrayList<>(source.parsers);
        this.shouldShow = new ArrayList<>(source.shouldShow);
        this.stripFormatting = source.stripFormatting;
        this.mode = source.mode;
    }

    public void prepare(@Nonnull PlayerContext context) {
        Resolver resolver = context.resolver();
        text = resolver.apply(text);
        shouldShow = shouldShow.stream().map(resolver).collect(Collectors.toList());
    }

    /**
     * Parses the result of a Component.
     * @param result The result to parse
     * @return The parsed result as a String. If not overridden, simply returns the {@link JsonPrimitive} result as a String.
     */
    @Nonnull
    public String parse(FormPlayer player, String result) {
        String value = result;
        for (Parser parser : parsers) {
            value = parser.parse(player, this, value);
        }
        return value;
    }

    @Nonnull
    public abstract String resultIfHidden();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomComponent)) return false;
        if (!super.equals(o)) return false;
        CustomComponent that = (CustomComponent) o;
        return text.equals(that.text) && parsers.equals(that.parsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text, parsers);
    }
}
