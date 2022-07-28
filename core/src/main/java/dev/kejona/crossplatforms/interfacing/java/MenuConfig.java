package dev.kejona.crossplatforms.interfacing.java;

import dev.kejona.crossplatforms.config.Configuration;
import dev.kejona.crossplatforms.interfacing.InterfaceConfig;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.util.Collections;
import java.util.Map;

import static dev.kejona.crossplatforms.utils.ConfigurateUtils.ACTION_TRANSLATOR;
import static org.spongepowered.configurate.NodePath.path;
import static org.spongepowered.configurate.transformation.ConfigurationTransformation.WILDCARD_OBJECT;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class MenuConfig extends InterfaceConfig {

    public static final int VERSION = 2;
    public static final int MINIMUM_VERSION = 1;

    private Map<String, JavaMenu> menus = Collections.emptyMap();

    public static ConfigurationTransformation.Versioned updater() {
        return ConfigurationTransformation.versionedBuilder()
                .versionKey(Configuration.VERSION_KEY)
                .addVersion(2, update1_2())
                .build();
    }

    private static ConfigurationTransformation update1_2() {
        ConfigurationTransformation.Builder builder = ConfigurationTransformation.builder();
        builder.addAction(path("menus", WILDCARD_OBJECT, "buttons", WILDCARD_OBJECT, "right-click"), ACTION_TRANSLATOR);
        builder.addAction(path("menus", WILDCARD_OBJECT, "buttons", WILDCARD_OBJECT, "left-click"), ACTION_TRANSLATOR);
        builder.addAction(path("menus", WILDCARD_OBJECT, "buttons", WILDCARD_OBJECT, "any-click"), ACTION_TRANSLATOR);
        return builder.build();
    }
}
