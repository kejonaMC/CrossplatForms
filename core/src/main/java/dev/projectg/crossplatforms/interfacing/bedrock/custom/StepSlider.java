package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.component.StepSliderComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
    public CustomComponent withPlaceholders(@Nonnull Function<String, String> resolver) {
        StepSlider stepSlider = new StepSlider();
        stepSlider.type = this.type;
        stepSlider.text = resolver.apply(this.text);
        for (String option : this.steps) {
            stepSlider.steps.add(resolver.apply(option));
        }
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
}
