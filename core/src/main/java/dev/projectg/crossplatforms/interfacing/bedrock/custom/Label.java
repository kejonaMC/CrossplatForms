package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.Resolver;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.LabelComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ToString(callSuper = true)
@ConfigSerializable
public class Label extends CustomComponent {

    public static final String TYPE = "label";

    @Inject
    private Label() {
        super("");
    }

    @Override
    public Label copy() {
        Label label = new Label();
        label.copyBasics(this);
        return label;
    }

    @Override
    public Component cumulusComponent() {
        return LabelComponent.of(text);
    }

    @Override
    public Label withPlaceholders(Resolver resolver) {
        Label copy = copy();
        copy.placeholders(resolver);
        return copy;
    }

    @Override
    public String type() {
        return TYPE;
    }
}
