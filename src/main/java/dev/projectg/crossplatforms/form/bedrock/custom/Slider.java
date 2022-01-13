package dev.projectg.crossplatforms.form.bedrock.custom;

import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.SliderComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Function;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Slider extends CustomComponent implements SliderComponent {

    private float min = 0;
    private float max = 10;
    private int step = 1;
    private float defaultValue = 0;

    @Override
    public CustomComponent withPlaceholders(Function<String, String> resolver) {
        return this; // technically should be a new instance, but this is immutable currently
    }
}
