package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import dev.projectg.crossplatforms.IllegalValueException;
import dev.projectg.crossplatforms.Resolver;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.parser.Parser;
import dev.projectg.crossplatforms.serialize.ValuedType;
import dev.projectg.crossplatforms.utils.ParseUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public abstract class CustomComponent implements ValuedType {

    @Getter
    protected String type = "";

    @Getter
    protected String text = "";

    @Nullable
    private String shouldShow = null;

    @Getter
    @Setter
    private List<Parser> parsers = new ArrayList<>(0);

    /**
     * Implementing classes should provide a zero arg constructor that calls super the constructor below
     */
    @SuppressWarnings("unused")
    private CustomComponent() {
        //no-op
    }

    protected CustomComponent(@Nonnull String type) {
        this.type = Objects.requireNonNull(type);
    }

    protected CustomComponent(@Nonnull String type, @Nonnull String text, @Nullable String shouldShow) {
        this.type = Objects.requireNonNull(type);
        this.text = Objects.requireNonNull(text);
        this.shouldShow = shouldShow;
    }

    public boolean show() {
        if (shouldShow == null) {
            return true;
        } else {
            return ParseUtils.getBoolean(shouldShow, true);
        }
    }

    public abstract CustomComponent copy();

    public abstract Component cumulusComponent() throws IllegalValueException;

    /**
     * Copies data in a source {@link CustomComponent} or any of its parent classes into a target.
     */
    protected final void copyBasics(CustomComponent source) {
        this.type = source.type;
        this.text = source.text;
        this.parsers = new ArrayList<>(source.parsers);
    }

    /**
     * Sets placeholders
     * @param resolver A map of placeholder (including % prefix and suffix), to the resolved value
     */
    public void placeholders(@Nonnull Resolver resolver) {
        Objects.requireNonNull(resolver);
        text = resolver.apply(text);
        if (shouldShow != null) {
            shouldShow = resolver.apply(shouldShow);
        }
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

    @Nonnull
    public abstract String resultIfHidden();
}
