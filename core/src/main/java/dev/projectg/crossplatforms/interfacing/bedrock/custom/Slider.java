package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.IllegalValueException;
import dev.projectg.crossplatforms.Resolver;
import dev.projectg.crossplatforms.utils.ParseUtils;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.SliderComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Slider extends CustomComponent {

    public static final String TYPE = "slider";

    private String min = "0";
    private String max = "10";
    private String step = "1";
    private String defaultValue = "0";

    @Inject
    private Slider() {
        super();
    }

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
    public Component cumulusComponent() throws IllegalValueException {
        return SliderComponent.of(
            text,
            ParseUtils.getFloat(min, "min"),
            ParseUtils.getFloat(max, "max"),
            ParseUtils.getUnsignedInt(step, "step"),
            ParseUtils.getFloat(defaultValue, "default-value")
        );
    }

    @Override
    public void placeholders(@Nonnull Resolver resolver) {
        super.placeholders(resolver);
        min = resolver.apply(min);
        max = resolver.apply(max);
        step = resolver.apply(step);
        defaultValue = resolver.apply(defaultValue);
    }

    @Override
    public Slider withPlaceholders(Resolver resolver) {
        Slider copy = copy();
        copy.placeholders(resolver);
        return copy;
    }

    @Nonnull
    @Override
    public String resultIfHidden() {
        return defaultValue;
    }

    @Override
    public String type() {
        return TYPE;
    }
}
