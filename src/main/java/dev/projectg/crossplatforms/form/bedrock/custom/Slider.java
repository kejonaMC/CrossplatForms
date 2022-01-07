package dev.projectg.crossplatforms.form.bedrock.custom;

import lombok.Getter;
import org.geysermc.cumulus.component.SliderComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Function;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Slider extends Component implements SliderComponent {

    private float min = 0;
    private float max = 10;
    private int step = 1;
    private float defaultValue = 0;

    @Override
    public Component withPlaceholders(Function<String, String> resolver) {
        return this; // technically should be a new instance, but this is immutable currently
    }
}
