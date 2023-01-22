package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import com.google.inject.Inject;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.LabelComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;

@ToString(callSuper = true)
@ConfigSerializable
public class Label extends CustomComponent {

    public static final String TYPE = "label";

    @Inject
    private Label() {
        super();
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

    @Nonnull
    @Override
    public String resultIfHidden() {
        return text;
    }

    @Override
    public String type() {
        return TYPE;
    }
}
