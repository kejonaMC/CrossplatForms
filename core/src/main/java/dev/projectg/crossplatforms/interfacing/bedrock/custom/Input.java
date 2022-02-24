package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.InputComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.function.Function;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Input extends CustomComponent implements InputComponent {

    private String placeholder = "";
    private String defaultText = "";

    @Override
    public CustomComponent withPlaceholders(@Nonnull Function<String, String> resolver) {
        Input input = new Input();
        input.type = this.type;
        input.text = resolver.apply(this.text);
        input.defaultText = resolver.apply(this.defaultText);
        input.placeholder = resolver.apply(this.placeholder);
        return input;
    }
}
