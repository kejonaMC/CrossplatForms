package dev.projectg.crossplatforms.config.mapping;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Getter
@ConfigSerializable
public abstract class Configuration {

    @Required
    @Setting("config-version")
    private int version;

    public abstract int getDefaultVersion();
}
