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
import java.util.Map;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class FormConfig extends InterfaceConfig {

    public static final int VERSION = 2;
    public static final int MINIMUM_VERSION = 1;

    private Map<String, BedrockForm> forms = Collections.emptyMap();

    public static ConfigurationTransformation.Versioned updater() {
        return ConfigurationTransformation.versionedBuilder()
                .addVersion(2, update1_2())
                .build();
    }

    private static ConfigurationTransformation update1_2() {
        NodePath wildcardForm = NodePath.path("forms", ConfigurationTransformation.WILDCARD_OBJECT);
        ConfigurationTransformation.Builder builder = ConfigurationTransformation.builder()
                // Custom forms
                .addAction(wildcardForm.withAppendedChild("action"), TransformAction.rename("actions"));

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
            return null;
        }));

        // Modal forms
        for (String button : new String[]{"button1", "button2"}) {
            builder.addAction(wildcardForm.withAppendedChild(button), ((path, value) -> {
                ConfigurateUtils.moveChildren(
                        value,
                        (o -> "server".equals(o) || "commands".equals(o) || "form".equals(o)),
                        "actions"
                );
                return null;
            }));
        }
        return builder.build();
    }
}
