package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import dev.projectg.crossplatforms.Resolver;
import dev.projectg.crossplatforms.config.serializer.ValuedType;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.util.ComponentType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.Objects;

@ToString
@Getter
@ConfigSerializable
public abstract class CustomComponent extends ValuedType implements org.geysermc.cumulus.component.Component {

    protected String text = "";

    /**
     * This protected no-arg constructor should ONLY be used for object-mapping in deserialization.
     * The zero-arg constructor in concrete child classes can be private if mapped with Configurate.
     */
    protected CustomComponent() {
        //no-op
    }

    public CustomComponent(@Nonnull ComponentType type, @Nonnull String text) {
        super(type.getName());
        this.text = text;
    }

    public abstract CustomComponent copy();

    /**
     * Copies data in a source {@link CustomComponent} or any of its parent classes into a target.
     */
    protected final void copyBasics(CustomComponent source, CustomComponent target) {
        target.type = source.type;
        target.text = source.text;
    }

    /**
     * Sets placeholders
     * @param resolver A map of placeholder (including % prefix and suffix), to the resolved value
     */
    public void setPlaceholders(@Nonnull Resolver resolver) {
        Objects.requireNonNull(resolver);
        this.text = resolver.apply(text);
    }

    /**
     * @return The instance with placeholders set
     */
    public static CustomComponent withPlaceholders(CustomComponent component, Resolver resolver) {
        CustomComponent resolved = component.copy();
        resolved.setPlaceholders(resolver);
        return resolved;
    }

    /**
     * Parses the result of a Component.
     * @param result The result to parse
     * @return The parsed result as a String. If not overridden, simply returns the {@link JsonPrimitive} result as a String.
     */
    public String parse(JsonPrimitive result) {
        return result.getAsString();
    }
}
