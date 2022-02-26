package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.util.ComponentType;
import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.function.Function;

@ToString
@Getter
@ConfigSerializable
public abstract class CustomComponent implements org.geysermc.cumulus.component.Component {

    @Required
    protected ComponentType type;
    protected String text = "";

    /**
     * This protected no-arg constructor should ONLY be used for object-mapping in deserialization.
     * The zero-arg constructor in concrete child classes can be private if mapped with Configurate.
     */
    protected CustomComponent() {
        //no-op
    }

    public CustomComponent(@Nonnull ComponentType type, @Nonnull String text) {
        this.type = type;
        this.text = text;
    }

    /**
     * Returns a new instance of the Component with any placeholders set
     * @param resolver A map of placeholder (including % prefix and suffix), to the resolved value
     * @return A new instance with placeholders resolved.
     */
    @Contract(pure = true)
    public abstract CustomComponent withPlaceholders(@Nonnull Function<String, String> resolver);

    /**
     * Parses the result of a Component.
     * @param result The result to parse
     * @return The parsed result as a String. If not overridden, simply returns the {@link JsonPrimitive} result as a String.
     */
    public String parse(JsonPrimitive result) {
        return result.getAsString();
    }
}
