package dev.projectg.crossplatforms.form.bedrock.custom;

import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.util.ComponentType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.function.Function;

@ToString
@Getter
@ConfigSerializable
public abstract class CustomComponent implements org.geysermc.cumulus.component.Component {

    @Required
    protected ComponentType type;
    protected String text = "";

    /**
     * Returns a new instance of the Component with any placeholders set
     * @param resolver A map of placeholder (including % prefix and suffix), to the resolved value
     * @return A new instance with placeholders resolved.
     */
    public abstract CustomComponent withPlaceholders(Function<String, String> resolver);
}
