package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import dev.projectg.crossplatforms.IllegalValueException;
import dev.projectg.crossplatforms.Resolver;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.parser.Parser;
import dev.projectg.crossplatforms.serialize.ValuedType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public abstract class CustomComponent implements ValuedType {

    protected String text = "";

    @Setter
    private List<Parser> parsers = new ArrayList<>(0);

    /**
     * This protected no-arg constructor should ONLY be used for object-mapping in deserialization.
     * The zero-arg constructor in concrete child classes can be private if mapped with Configurate.
     */
    protected CustomComponent() {
        //no-op
    }

    public CustomComponent(@Nonnull String text) {
        this.text = Objects.requireNonNull(text);
    }

    public abstract CustomComponent copy();

    public abstract Component cumulusComponent() throws IllegalValueException;

    /**
     * Copies data in a source {@link CustomComponent} or any of its parent classes into a target.
     */
    protected final void copyBasics(CustomComponent source) {
        this.text = source.text;
        this.parsers = new ArrayList<>(source.parsers);
    }

    /**
     * Sets placeholders
     * @param resolver A map of placeholder (including % prefix and suffix), to the resolved value
     */
    public void placeholders(@Nonnull Resolver resolver) {
        Objects.requireNonNull(resolver);
        this.text = resolver.apply(text);
    }

    /**
     * @return A new instance with placeholders set
     */
    public abstract CustomComponent withPlaceholders(Resolver resolver);

    /**
     * Parses the result of a Component.
     * @param result The result to parse
     * @return The parsed result as a String. If not overridden, simply returns the {@link JsonPrimitive} result as a String.
     */
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

}
