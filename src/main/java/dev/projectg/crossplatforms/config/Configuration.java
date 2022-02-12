package dev.projectg.crossplatforms.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Getter
@ConfigSerializable
public abstract class Configuration {

    public static final String VERSION_KEY = "config-version";

    @Required
    @Setting(VERSION_KEY)
    private int version;

    public abstract int getDefaultVersion();
}
