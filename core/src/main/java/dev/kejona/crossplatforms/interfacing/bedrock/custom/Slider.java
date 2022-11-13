package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.IllegalValueException;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.utils.ParseUtils;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.SliderComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
public class Slider extends CustomComponent {

    public static final String TYPE = "slider";

    private String min = "0";
    private String max = "10";
    private String step = "1";
    private String defaultValue = "0";

    @Inject
    private Slider() {

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
            ParseUtils.getPositiveFloat(step, "step"),
            ParseUtils.getFloat(defaultValue, "default-value")
        );
    }

    @Override
    public void prepare(@Nonnull Resolver resolver) {
        super.prepare(resolver);
        min = resolver.apply(min);
        max = resolver.apply(max);
        step = resolver.apply(step);
        defaultValue = resolver.apply(defaultValue);
    }

    @Override
    public Slider preparedCopy(Resolver resolver) {
        Slider copy = copy();
        copy.prepare(resolver);
        return copy;
    }

    @Nonnull
    @Override
    public String parse(FormPlayer player, String result) {
        return super.parse(player, ParseUtils.downSize(result));
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
