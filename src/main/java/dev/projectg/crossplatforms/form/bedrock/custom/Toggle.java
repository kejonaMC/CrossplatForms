package dev.projectg.crossplatforms.form.bedrock.custom;

import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.ToggleComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Function;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Toggle extends CustomComponent implements ToggleComponent {

    private boolean defaultValue = false;

    @Override
    public CustomComponent withPlaceholders(Function<String, String> resolver) {
        Toggle toggle = new Toggle();
        toggle.type = this.type;
        toggle.text = resolver.apply(this.text);
        toggle.defaultValue = this.defaultValue;

        return toggle;
    }
}
