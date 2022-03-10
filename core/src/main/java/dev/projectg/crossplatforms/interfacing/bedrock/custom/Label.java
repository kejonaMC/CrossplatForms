package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.Resolver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.LabelComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;

@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigSerializable
public class Label extends CustomComponent {

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
    public void setPlaceholders(@Nonnull Resolver resolver) {
        super.setPlaceholders(resolver);
    }

    @Override
    public Label withPlaceholders(Resolver resolver) {
        Label copy = copy();
        copy.setPlaceholders(resolver);
        return copy;
    }
}
