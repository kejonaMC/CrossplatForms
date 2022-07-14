package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.IllegalValueException;
import dev.kejona.crossplatforms.Resolver;
import dev.kejona.crossplatforms.filler.Filler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.utils.ParseUtils;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.DropdownComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Dropdown extends CustomComponent {

    public static final String TYPE = "dropdown";

    private List<String> options = new ArrayList<>();
    private String defaultOption = "0";

    /**
     * Whether or not the parsing of the Dropdown should return the index of the selection or the text of the button.
     */
    private boolean returnText = true;

    private Filler filler = null;

    @Inject
    private Dropdown() {
        super();
    }

    @Override
    public Dropdown copy() {
        Dropdown dropdown = new Dropdown();
        dropdown.copyBasics(this);
        dropdown.options = new ArrayList<>(this.options);
        dropdown.defaultOption = this.defaultOption;
        dropdown.returnText = this.returnText;
        dropdown.filler = this.filler;
        return dropdown;
    }

    @Override
    public Component cumulusComponent() throws IllegalValueException {
        return DropdownComponent.of(text, options, ParseUtils.getUnsignedInt(defaultOption, "default-option"));
    }

    @Override
    public void prepare(@Nonnull Resolver resolver) {
        super.prepare(resolver);
        if (filler != null) {
            Collection<String> generated = filler.generate();
            if (filler.insertBefore()) {
                options.addAll(0, generated);
            } else {
                options.addAll(generated);
            }
        }

        options = options.stream().map(resolver).collect(Collectors.toList());
        defaultOption = resolver.apply(defaultOption);
    }

    @Override
    public Dropdown preparedCopy(Resolver resolver) {
        Dropdown copy = copy();
        copy.prepare(resolver);
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

    @Nonnull
    @Override
    public String resultIfHidden() {
        return defaultOption;
    }

    @Override
    public String type() {
        return TYPE;
    }
}
