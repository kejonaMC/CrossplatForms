package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.StepSliderComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class StepSlider extends CustomComponent implements StepSliderComponent {

    private List<String> steps = new ArrayList<>();
    private int defaultStep = 0;

    @Override
    public CustomComponent withPlaceholders(@Nonnull Function<String, String> resolver) {
        StepSlider stepSlider = new StepSlider();
        stepSlider.type = this.type;
        stepSlider.text = resolver.apply(this.text);
        for (String option : this.steps) {
            stepSlider.steps.add(resolver.apply(option));
        }
        stepSlider.defaultStep = this.defaultStep;
        return stepSlider;
    }
}
