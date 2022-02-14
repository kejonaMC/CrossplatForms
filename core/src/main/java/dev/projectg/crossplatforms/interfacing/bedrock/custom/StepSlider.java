package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.StepSliderComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class StepSlider extends CustomComponent implements StepSliderComponent {

    private List<String> steps = Collections.emptyList();
    private int defaultStep = 0;

    @Override
    public CustomComponent withPlaceholders(Function<String, String> resolver) {
        StepSlider stepSlider = new StepSlider();
        stepSlider.type = this.type;
        stepSlider.defaultStep = this.defaultStep;
        for (String option : this.steps) {
            stepSlider.steps.add(resolver.apply(option));
        }

        return stepSlider;
    }
}
