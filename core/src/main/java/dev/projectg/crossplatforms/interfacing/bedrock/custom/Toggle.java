package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.Resolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.component.ToggleComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.Locale;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Toggle extends CustomComponent {

    public static final String TYPE = "toggle";

    private String defaultValue = "false";

    @Override
    public ToggleComponent cumulusComponent() throws IllegalValueException {
        String lower = defaultValue.toLowerCase(Locale.ROOT);
        if (lower.equals("true") || lower.equals("false")) {
            return ToggleComponent.of(text, Boolean.parseBoolean(lower));
        } else {
            throw new IllegalValueException(lower, "boolean", "default-value");
        }
    }

    @Override
    public void placeholders(@Nonnull Resolver resolver) {
        super.placeholders(resolver);
        defaultValue = resolver.apply(defaultValue);
    }

    @Override
    public Toggle copy() {
        Toggle toggle = new Toggle();
        toggle.copyBasics(this);
        toggle.defaultValue = this.defaultValue;
        return toggle;
    }

    @Override
    public Toggle withPlaceholders(Resolver resolver) {
        Toggle copy = copy();
        copy.placeholders(resolver);
        return copy;
    }
}
