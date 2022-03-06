package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.Resolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.cumulus.component.SliderComponent;
import org.geysermc.cumulus.util.ComponentType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Slider extends CustomComponent implements SliderComponent {

    private float min = 0;
    private float max = 10;
    private int step = 1;
    private float defaultValue = 0;

    @Override
    public void setPlaceholders(@Nonnull Resolver resolver) {
        super.setPlaceholders(resolver);
    }

    @Override
    public CustomComponent copy() {
        Slider slider = new Slider();
        copyBasics(this, slider);
        slider.min = this.min;
        slider.max = this.max;
        slider.step = this.step;
        slider.defaultValue = this.defaultValue;
        return slider;    }

    @Override
    public @NonNull ComponentType getType() {
        return ComponentType.SLIDER;
    }
}
