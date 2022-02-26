package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.command.proxy.ProxyCommand;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Collections;
import java.util.Map;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class GeneralConfig extends Configuration {

    public static final int VERSION = 1;
    public static final int MINIMUM_VERSION = 1;

    private Map<String, ProxyCommand> commands = Collections.emptyMap();

    private boolean unsafeCommandRegistration = false;

    @Setting("enable-debug")
    private boolean enableDebug = false;
}
