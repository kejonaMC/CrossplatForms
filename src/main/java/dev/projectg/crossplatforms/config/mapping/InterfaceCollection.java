package dev.projectg.crossplatforms.config.mapping;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Map;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public abstract class InterfaceCollection<T> extends Configuration {

    private boolean enable = true;

    @Required
    private Map<String, T> elements;
}
