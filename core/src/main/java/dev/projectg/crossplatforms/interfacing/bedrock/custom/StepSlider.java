package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.Resolver;
import dev.projectg.crossplatforms.handler.FormPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.component.StepSliderComponent;
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
public class StepSlider extends CustomComponent {

    private List<String> steps = new ArrayList<>();
    private int defaultStep = 0;

    /**
     * Whether or not the parsing of the Dropdown should return the index of the selection or the text of the button.
     */
    private boolean returnText = true;

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
    public StepSliderComponent cumulusComponent() {
        return StepSliderComponent.of(text, steps, defaultStep);
    }

    @Override
    public void setPlaceholders(@Nonnull Resolver resolver) {
        super.setPlaceholders(resolver);
        steps = steps.stream().map(resolver).collect(Collectors.toList());
    }

    @Override
    public StepSlider withPlaceholders(Resolver resolver) {
        StepSlider copy = copy();
        copy.setPlaceholders(resolver);
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
}
