package dev.kejona.crossplatforms.interfacing.java;


import com.google.inject.Inject;
import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.inventory.ConfiguredItem;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ItemButton extends ConfiguredItem {

    public static final String STATIC_IDENTIFIER = "crossplatformsbutton";

    private List<Action<? super JavaMenu>> anyClick = Collections.emptyList();
    private List<Action<? super JavaMenu>> leftClick = Collections.emptyList();
    private List<Action<? super JavaMenu>> rightClick = Collections.emptyList();

    @Inject
    private ItemButton() {
        super();
    }
}
