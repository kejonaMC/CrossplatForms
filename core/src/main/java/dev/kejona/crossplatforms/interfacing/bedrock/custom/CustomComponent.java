package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import dev.kejona.crossplatforms.IllegalValueException;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.OptionalElement;
import dev.kejona.crossplatforms.parser.Parser;
import dev.kejona.crossplatforms.serialize.ValuedType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ToString
public abstract class CustomComponent extends OptionalElement implements ValuedType {

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

    public abstract CustomComponent copy();

    public abstract CustomComponent preparedCopy(Resolver resolver);

    public abstract Component cumulusComponent() throws IllegalValueException;

    @Nonnull
    public abstract String resultIfHidden();

    /**
     * Copies data in a source {@link CustomComponent} or any of its parent classes into a target.
     */
    protected final void copyBasics(CustomComponent source) {
        this.text = source.text;
        this.parsers = new ArrayList<>(source.parsers);
        this.shouldShow = new ArrayList<>(source.shouldShow);
    }

    /**
     * Sets placeholders
     * @param resolver A map of placeholder (including % prefix and suffix), to the resolved value
     */
    public void prepare(@Nonnull Resolver resolver) {
        Objects.requireNonNull(resolver);
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

    public void parser(Parser parser) {
        parsers.add(parser);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomComponent component = (CustomComponent) o;
        return text.equals(component.text) && Objects.equals(shouldShow, component.shouldShow) && parsers.equals(component.parsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, shouldShow, parsers);
    }
}
