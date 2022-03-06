package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import dev.projectg.crossplatforms.Resolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.cumulus.component.StepSliderComponent;
import org.geysermc.cumulus.util.ComponentType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class StepSlider extends CustomComponent implements StepSliderComponent {

    private List<String> steps = new ArrayList<>();
    private int defaultStep = 0;

    /**
     * Whether or not the parsing of the Dropdown should return the index of the selection or the text of the button.
     */
    private boolean returnText = true;

    @Override
    public void setPlaceholders(@Nonnull Resolver resolver) {
        super.setPlaceholders(resolver);
        steps = steps.stream().map(resolver).collect(Collectors.toList());
    }

    @Override
    public StepSlider copy() {
        StepSlider stepSlider = new StepSlider();
        copyBasics(this, stepSlider);
        stepSlider.steps = new ArrayList<>(steps);
        stepSlider.defaultStep = this.defaultStep;
        stepSlider.returnText = this.returnText;
        return stepSlider;
    }

    @Override
    public String parse(JsonPrimitive result) {
        if (returnText) {
            return steps.get(result.getAsInt());
        } else {
            return super.parse(result);
        }
    }

    @Override
    public @NonNull ComponentType getType() {
        return ComponentType.STEP_SLIDER;
    }
}
