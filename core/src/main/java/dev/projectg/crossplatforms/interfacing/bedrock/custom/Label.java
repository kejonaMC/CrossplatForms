package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.Resolver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.LabelComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;

@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigSerializable
public class Label extends CustomComponent {

    public static final String TYPE = "label";

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
    public void placeholders(@Nonnull Resolver resolver) {
        super.placeholders(resolver);
    }

    @Override
    public Label withPlaceholders(Resolver resolver) {
        Label copy = copy();
        copy.placeholders(resolver);
        return copy;
    }
}
