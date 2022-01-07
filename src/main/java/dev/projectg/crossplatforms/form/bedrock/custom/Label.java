package dev.projectg.crossplatforms.form.bedrock.custom;

import org.geysermc.cumulus.component.LabelComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Function;

@ConfigSerializable
public class Label extends Component implements LabelComponent {
    // text is handled in Component super class (both here and in Cumulus)

    @Override
    public Component withPlaceholders(Function<String, String> resolver) {
        Label label = new Label();
        label.type = this.type;
        label.text = resolver.apply(this.text);
        return label;
    }
}
