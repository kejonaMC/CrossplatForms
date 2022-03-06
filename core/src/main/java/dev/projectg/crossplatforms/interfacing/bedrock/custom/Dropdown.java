package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import dev.projectg.crossplatforms.Resolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.cumulus.component.DropdownComponent;
import org.geysermc.cumulus.util.ComponentType;
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
public class Dropdown extends CustomComponent implements DropdownComponent {

    private List<String> options = new ArrayList<>();
    private int defaultOption = 0;

    /**
     * Whether or not the parsing of the Dropdown should return the index of the selection or the text of the button.
     */
    private boolean returnText = true;

    @Override
    public Dropdown copy() {
        Dropdown dropdown = new Dropdown();
        copyBasics(this, dropdown);
        dropdown.options = new ArrayList<>(this.options);
        dropdown.defaultOption = this.defaultOption;
        dropdown.returnText = this.returnText;
        return dropdown;
    }

    @Override
    public void setPlaceholders(@Nonnull Resolver resolver) {
        super.setPlaceholders(resolver);
        options = options.stream().map(resolver).collect(Collectors.toList());
    }

    @Override
    public String parse(JsonPrimitive result) {
        if (returnText) {
            return options.get(result.getAsInt());
        } else {
            return super.parse(result);
        }
    }

    @Override
    public @NonNull ComponentType getType() {
        return ComponentType.DROPDOWN;
    }
}
