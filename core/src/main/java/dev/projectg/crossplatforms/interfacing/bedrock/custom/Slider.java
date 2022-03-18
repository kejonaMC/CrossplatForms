package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.Resolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.SliderComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Slider extends CustomComponent {

    public static final String TYPE = "slider";

    private float min = 0;
    private float max = 10;
    private int step = 1;
    private float defaultValue = 0;

    @Override
    public Slider copy() {
        Slider slider = new Slider();
        slider.copyBasics(this);
        slider.min = this.min;
        slider.max = this.max;
        slider.step = this.step;
        slider.defaultValue = this.defaultValue;
        return slider;
    }

    @Override
    public Component cumulusComponent() {
        return SliderComponent.of(text, min, max, step, defaultValue);
    }

    @Override
    public void placeholders(@Nonnull Resolver resolver) {
        super.placeholders(resolver);
    }

    @Override
    public Slider withPlaceholders(Resolver resolver) {
        Slider copy = copy();
        copy.placeholders(resolver);
        return copy;
    }
}
