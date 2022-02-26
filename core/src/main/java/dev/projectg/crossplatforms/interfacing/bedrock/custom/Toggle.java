package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.component.ToggleComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.function.Function;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Toggle extends CustomComponent implements ToggleComponent {

    private boolean defaultValue = false;

    @Override
    public CustomComponent withPlaceholders(@Nonnull Function<String, String> resolver) {
        Toggle toggle = new Toggle();
        toggle.type = this.type;
        toggle.text = resolver.apply(this.text);
        toggle.defaultValue = this.defaultValue;
        return toggle;
    }
}
