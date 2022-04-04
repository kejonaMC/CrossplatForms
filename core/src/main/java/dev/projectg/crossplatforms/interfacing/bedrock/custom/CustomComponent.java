package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import dev.projectg.crossplatforms.Resolver;
import dev.projectg.crossplatforms.config.serializer.ValuedType;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.parser.Parser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.util.ComponentType;
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

    protected String type = "";
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

    public CustomComponent(ComponentType type, @Nonnull String text) {
        this.type = Objects.requireNonNull(type.getName()); // lowercase it for serialization to work in cumulus
        this.text = Objects.requireNonNull(text);
    }

    public abstract CustomComponent copy();

    public abstract Component cumulusComponent() throws IllegalValueException;

    /**
     * Copies data in a source {@link CustomComponent} or any of its parent classes into a target.
     */
    protected final void copyBasics(CustomComponent source) {
        this.type = source.type;
        this.text = source.text;
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

    public static int getInt(String value, String identifier) throws IllegalValueException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalValueException(value, "integer", identifier);
        }
    }

    public static int getUnsignedInt(String value, String identifier) throws IllegalValueException {
        try {
            return Integer.parseUnsignedInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalValueException(value, "non-negative integer", identifier);
        }
    }

    public static float getFloat(String value, String identifier) throws IllegalValueException {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new IllegalValueException(value, "decimal number", identifier);
        }
    }
}
