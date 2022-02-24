package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import lombok.ToString;
import org.geysermc.cumulus.component.LabelComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.function.Function;

@ToString
@ConfigSerializable
public class Label extends CustomComponent implements LabelComponent {
    // text is handled in Component super class (both here and in Cumulus)

    @Override
    public CustomComponent withPlaceholders(@Nonnull Function<String, String> resolver) {
        Label label = new Label();
        label.type = this.type;
        label.text = resolver.apply(this.text);
        return label;
    }
}
