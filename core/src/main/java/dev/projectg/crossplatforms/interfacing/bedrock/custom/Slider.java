package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.SliderComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
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
    public CustomComponent withPlaceholders(@Nonnull Function<String, String> resolver) {
        Slider slider = new Slider();
        slider.type = this.type;
        slider.text = resolver.apply(this.text);
        slider.min = this.min;
        slider.max = this.max;
        slider.step = this.step;
        slider.defaultValue = this.defaultValue;
        return slider;
    }
}
