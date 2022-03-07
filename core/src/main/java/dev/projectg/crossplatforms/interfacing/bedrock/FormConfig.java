package dev.projectg.crossplatforms.interfacing.bedrock;

import dev.projectg.crossplatforms.interfacing.InterfaceConfig;
import dev.projectg.crossplatforms.utils.ConfigurateUtils;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class FormConfig extends InterfaceConfig {

    public static final int VERSION = 3;
    public static final int MINIMUM_VERSION = 1;

    private Map<String, BedrockForm> forms = Collections.emptyMap();

    public static ConfigurationTransformation.Versioned updater() {
        return ConfigurationTransformation.versionedBuilder()
                .versionKey("config-version")
                .addVersion(2, update1_2())
                .addVersion(3, update2_3())
                .build();
    }

    private static ConfigurationTransformation update1_2() {
        NodePath wildcardForm = NodePath.path("forms", ConfigurationTransformation.WILDCARD_OBJECT);
        ConfigurationTransformation.Builder builder = ConfigurationTransformation.builder();

        // Custom forms
        builder.addAction(wildcardForm.withAppendedChild("action"), TransformAction.rename("actions"));

        // Simple forms
        builder.addAction(wildcardForm.withAppendedChild("buttons"), ((path, value) -> {
            List<? extends ConfigurationNode> buttons = value.childrenList();
            if (buttons != null) {
                for (ConfigurationNode button : buttons) {
                    ConfigurateUtils.moveChildren(
                            button,
                            (o -> "server".equals(o) || "commands".equals(o) || "form".equals(o)),
                            "actions"
                    );
                }
            }
            return null; // don't move value
        }));

        // Modal forms
        for (String button : new String[]{"button1", "button2"}) {
            builder.addAction(wildcardForm.withAppendedChild(button), ((path, value) -> {
                ConfigurateUtils.moveChildren(
                        value,
                        (o -> "server".equals(o) || "commands".equals(o) || "form".equals(o)),
                        "actions"
                );
                return null; // don't move value
            }));
        }
        return builder.build();
    }

    private static ConfigurationTransformation update2_3() {
        ConfigurationTransformation.Builder builder = ConfigurationTransformation.builder();
        NodePath wildcardForm = NodePath.path("forms", ConfigurationTransformation.WILDCARD_OBJECT);

        // form types
        NodePath formType = wildcardForm.withAppendedChild("type");
        builder.addAction(formType, ((path, value) -> {
            String type = value.getString();
            if (type != null) {
                value.set(String.class, type.toLowerCase(Locale.ROOT));
            }
            return null; // don't move value
        }));

        // component types of custom forms
        NodePath componentType = wildcardForm.plus(NodePath.path("components", ConfigurationTransformation.WILDCARD_OBJECT, "type"));
        builder.addAction(componentType, ((path, value) -> {
            String type = value.getString();
            if (type != null) {
                value.set(String.class, type.toLowerCase(Locale.ROOT));
            }
            return null; // don't move value
        }));

        return builder.build();
    }
}
