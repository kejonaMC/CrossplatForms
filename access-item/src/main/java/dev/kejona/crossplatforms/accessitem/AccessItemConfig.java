package dev.projectg.crossplatforms.accessitem;

import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.config.Configuration;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;

import java.util.Collections;
import java.util.Map;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class AccessItemConfig extends Configuration {

    public static final int VERSION = 3;
    public static final int MINIMUM_VERSION = 1;

    private static final NodePath WILDCARD_ITEM = NodePath.path("items", ConfigurationTransformation.WILDCARD_OBJECT);

    private boolean enable = false;

    private boolean setHeldSlot = false;

    private final Map<AccessItem.Limit, PermissionDefault> globalPermissionDefaults = Collections.emptyMap();

    private Map<String, AccessItem> items = Collections.emptyMap();

    public static ConfigurationTransformation.Versioned updater() {
        return ConfigurationTransformation.versionedBuilder()
                .versionKey(Configuration.VERSION_KEY)
                .addVersion(2, update1_2())
                .addVersion(3, update2_3())
                .build();
    }

    private static ConfigurationTransformation update1_2() {
        return ConfigurationTransformation.builder()
                // move form value under action key (BasicClickAction)
                .addAction(WILDCARD_ITEM, (path, value) -> {
                    value.node("action", "form").set(value.node("form")); // under action
                    value.node("form").raw(null); // delete
                    return null; // don't move it since we did it manually
                })
                .build();
    }

    private static ConfigurationTransformation update2_3() {
        return ConfigurationTransformation.builder()
                .addAction(WILDCARD_ITEM.withAppendedChild("action"), TransformAction.rename("actions"))
                .addAction(WILDCARD_ITEM.withAppendedChild("bedrock-action"), TransformAction.rename("bedrock-actions"))
                .addAction(WILDCARD_ITEM.withAppendedChild("java-action"), TransformAction.rename("java-actions"))
                .build();
    }

    public static ConfigId asConfigId() {
        return ConfigId.builder()
            .file("access-items.yml")
            .version(VERSION)
            .minimumVersion(MINIMUM_VERSION)
            .clazz(AccessItemConfig.class)
            .updater(AccessItemConfig::updater)
            .build();
    }
}
