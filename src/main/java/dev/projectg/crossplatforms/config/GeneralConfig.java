package dev.projectg.crossplatforms.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Collections;
import java.util.Map;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class GeneralConfig extends Configuration {

    private final int defaultVersion = 1;

    @Setting("enable-debug")
    private boolean enableDebug = false;

    private Map<String, ProxyCommand> commands = Collections.emptyMap();
}
