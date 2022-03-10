package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.Resolver;
import dev.projectg.crossplatforms.handler.FormPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.DropdownComponent;
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
public class Dropdown extends CustomComponent {

    private List<String> options = new ArrayList<>();
    private int defaultOption = 0;

    /**
     * Whether or not the parsing of the Dropdown should return the index of the selection or the text of the button.
     */
    private boolean returnText = true;

    @Override
    public Dropdown copy() {
        Dropdown dropdown = new Dropdown();
        dropdown.copyBasics(this);
        dropdown.options = new ArrayList<>(this.options);
        dropdown.defaultOption = this.defaultOption;
        dropdown.returnText = this.returnText;
        return dropdown;
    }

    @Override
    public Component cumulusComponent() {
        return DropdownComponent.of(text, options, defaultOption);
    }

    @Override
    public void setPlaceholders(@Nonnull Resolver resolver) {
        super.setPlaceholders(resolver);
        options = options.stream().map(resolver).collect(Collectors.toList());
    }

    @Override
    public Dropdown withPlaceholders(Resolver resolver) {
        Dropdown copy = copy();
        copy.setPlaceholders(resolver);
        return copy;
    }

    @Override
    public String parse(FormPlayer player, String result) {
        if (returnText) {
            return super.parse(player, options.get(Integer.parseInt(result)));
        } else {
            return super.parse(player, result);
        }
    }
}
