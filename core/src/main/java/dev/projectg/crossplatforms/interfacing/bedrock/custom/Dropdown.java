package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.component.DropdownComponent;
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
public class Dropdown extends CustomComponent implements DropdownComponent {

    private List<String> options = new ArrayList<>();
    private int defaultOption = 0;

    /**
     * Whether or not the parsing of the Dropdown should return the index of the selection or the text of the button.
     */
    private boolean returnText = true;

    @Override
    public CustomComponent withPlaceholders(@Nonnull Function<String, String> resolver) {
        Dropdown dropdown = new Dropdown();
        dropdown.type = this.type;
        dropdown.text = resolver.apply(this.text);
        dropdown.defaultOption = this.defaultOption;
        for (String option : this.options) {
            dropdown.options.add(resolver.apply(option));
        }
        dropdown.returnText = this.returnText;
        return dropdown;
    }

    @Override
    public String parse(JsonPrimitive result) {
        if (returnText) {
            return options.get(result.getAsInt());
        } else {
            return super.parse(result);
        }
    }
}
