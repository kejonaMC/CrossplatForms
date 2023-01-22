package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.IllegalValueException;
import dev.kejona.crossplatforms.context.PlayerContext;
import dev.kejona.crossplatforms.utils.ParseUtils;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.ToggleComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
public class Toggle extends CustomComponent {

    public static final String TYPE = "toggle";

    private String defaultValue = "false";

    @Inject
    private Toggle() {
        super();
    }

    @Override
    public ToggleComponent cumulusComponent() throws IllegalValueException {
        return ToggleComponent.of(text, ParseUtils.getBoolean(defaultValue, "default-value"));
    }

    @Override
    public void prepare(@Nonnull PlayerContext context) {
        super.prepare(context);
        defaultValue = context.resolver().apply(defaultValue);
    }

    @Override
    public Toggle copy() {
        Toggle toggle = new Toggle();
        toggle.copyBasics(this);
        toggle.defaultValue = this.defaultValue;
        return toggle;
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
