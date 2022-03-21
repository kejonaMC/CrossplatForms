package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.command.proxy.CustomCommand;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class GeneralConfig extends Configuration {

    public static final int VERSION = 1;
    public static final int MINIMUM_VERSION = 1;

    @Nullable
    private String rootCommand = null;

    @Nonnull
    private Map<String, CustomCommand> commands = Collections.emptyMap();

    private boolean unsafeCommandRegistration = false;

    @Setting("enable-debug")
    private boolean enableDebug = false;
}
