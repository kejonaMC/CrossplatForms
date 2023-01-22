package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.IllegalValueException;
import dev.kejona.crossplatforms.context.PlayerContext;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.filler.OptionFiller;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.utils.ParseUtils;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.StepSliderComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
public class StepSlider extends CustomComponent {

    public static final String TYPE = "step_slider";

    private List<Option> steps = new ArrayList<>();
    private String defaultStep = "0";
    private List<OptionFiller> fillers = Collections.emptyList();

    /**
     * Whether or not the parsing of the Dropdown should return the index of the selection or the text of the button.
     */
    private boolean returnText = true;

    @Inject
    private StepSlider() {
        super();
    }

    @Override
    public StepSlider copy() {
        StepSlider stepSlider = new StepSlider();
        stepSlider.copyBasics(this);
        stepSlider.steps = new ArrayList<>(steps);
        stepSlider.defaultStep = this.defaultStep;
        stepSlider.fillers = new ArrayList<>(this.fillers);
        stepSlider.returnText = this.returnText;
        return stepSlider;
    }

    @Override
    public StepSliderComponent cumulusComponent() throws IllegalValueException {
        return StepSliderComponent.of(
            text,
            steps.stream().map(Option::display).collect(Collectors.toList()),
            ParseUtils.getUnsignedInt(defaultStep, "default-step")
        );
    }

    @Override
    public void prepare(@Nonnull PlayerContext context) {
        super.prepare(context);
        // apply fillers
        for (OptionFiller filler : fillers) {
            filler.fillOptions(steps, context);
        }

        // apply placeholders
        Resolver resolver = context.resolver();
        steps = steps.stream().map(o -> o.with(resolver)).collect(Collectors.toList());
        defaultStep = resolver.apply(defaultStep);
    }

    @Nonnull
    @Override
    public String parse(FormPlayer player, String result) {
        if (returnText) {
            return super.parse(player, steps.get(Integer.parseInt(result)).returnText());
        } else {
            return super.parse(player, result);
        }
    }

    @Nonnull
    @Override
    public String resultIfHidden() {
        return defaultStep;
    }

    @Override
    public String type() {
        return TYPE;
    }
}
