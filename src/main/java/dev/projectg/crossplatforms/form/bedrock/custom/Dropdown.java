package dev.projectg.crossplatforms.form.bedrock.custom;

import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.DropdownComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Dropdown extends CustomComponent implements DropdownComponent {

    private List<String> options = Collections.emptyList();
    private int defaultOption = 0;

    @Override
    public CustomComponent withPlaceholders(Function<String, String> resolver) {
        Dropdown dropdown = new Dropdown();
        dropdown.type = this.type;
        dropdown.text = resolver.apply(this.text);
        dropdown.defaultOption = this.defaultOption;
        for (String option : this.options) {
            dropdown.options.add(resolver.apply(option));
        }

        return dropdown;
    }
}
