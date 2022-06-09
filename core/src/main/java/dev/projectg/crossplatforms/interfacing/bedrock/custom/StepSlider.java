package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.IllegalValueException;
import dev.projectg.crossplatforms.Resolver;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.utils.ParseUtils;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.StepSliderComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class StepSlider extends CustomComponent {

    public static final String TYPE = "step_slider";

    private List<String> steps = new ArrayList<>();
    private String defaultStep = "0";

    /**
     * Whether or not the parsing of the Dropdown should return the index of the selection or the text of the button.
     */
    private boolean returnText = true;

    @Inject
    private StepSlider() {
        super(TYPE);
    }

    @Override
    public StepSlider copy() {
        StepSlider stepSlider = new StepSlider();
        stepSlider.copyBasics(this);
        stepSlider.steps = new ArrayList<>(steps);
        stepSlider.defaultStep = this.defaultStep;
        stepSlider.returnText = this.returnText;
        return stepSlider;
    }

    @Override
    public StepSliderComponent cumulusComponent() throws IllegalValueException {
        return StepSliderComponent.of(text, steps, ParseUtils.getUnsignedInt(defaultStep, "default-step"));
    }

    @Override
    public void placeholders(@Nonnull Resolver resolver) {
        super.placeholders(resolver);
        steps = steps.stream().map(resolver).collect(Collectors.toList());
        defaultStep = resolver.apply(defaultStep);
    }

    @Override
    public StepSlider withPlaceholders(Resolver resolver) {
        StepSlider copy = copy();
        copy.placeholders(resolver);
        return copy;
    }

    @Override
    public String parse(FormPlayer player, String result) {
        if (returnText) {
            return steps.get(Integer.parseInt(result));
        } else {
            return super.parse(player, result);
        }
    }

    @Nonnull
    @Override
    public String resultIfHidden() {
        return defaultStep;
    }
}
