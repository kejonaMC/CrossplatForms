package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.command.custom.CustomCommand;
import lombok.Getter;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

import static org.spongepowered.configurate.transformation.ConfigurationTransformation.WILDCARD_OBJECT;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class GeneralConfig extends Configuration {

    public static final int VERSION = 2;
    public static final int MINIMUM_VERSION = 1;

    @Nullable
    private String rootCommand = null;

    @Nonnull
    private Map<String, CustomCommand> commands = Collections.emptyMap();

    private boolean unsafeCommandRegistration = false;

    @Setting("enable-debug")
    private boolean enableDebug = false;

    public static ConfigurationTransformation.Versioned updater() {
        return ConfigurationTransformation.versionedBuilder()
            .versionKey(Configuration.VERSION_KEY)
            .addVersion(2, update1_2())
            .build();
    }

    public static ConfigurationTransformation update1_2() {
        return ConfigurationTransformation.builder()
            .addAction(NodePath.path("commands", WILDCARD_OBJECT), ((path, value) -> {
                CommandType type = value.node("method").get(CommandType.class);
                if (type == CommandType.INTERCEPT_CANCEL || type == CommandType.INTERCEPT_PASS) {
                    value.node("exact").set(String.class, value.key());
                }
                return null;
            })).build();
    }
}
