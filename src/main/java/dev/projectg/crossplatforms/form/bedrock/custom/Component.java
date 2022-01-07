package dev.projectg.crossplatforms.form.bedrock.custom;

import lombok.Getter;
import org.geysermc.cumulus.util.ComponentType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Function;

@Getter
@ConfigSerializable
public abstract class Component implements org.geysermc.cumulus.component.Component {

    protected ComponentType type;
    protected String text;

    /**
     * Returns a new instance of the Component with any placeholders set
     * @param resolver A map of placeholder (including % prefix and suffix), to the resolved value
     * @return A new instance with placeholders resolved.
     */
    public abstract Component withPlaceholders(Function<String, String> resolver);
}
