package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.context.PlayerContext;
import dev.kejona.crossplatforms.interfacing.java.ItemButton;
import dev.kejona.crossplatforms.serialize.ValuedType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public interface InventoryFiller extends ValuedType {

    default void fillItems(Map<Integer, ItemButton> container, PlayerContext context) {
        ItemButton template = itemTemplate();

        Map<Integer, ItemButton> buttons = rawItems(context);
        if (template != null) {
            // Apply formatting
            for (Map.Entry<Integer, ItemButton> entry : buttons.entrySet()) {
                ItemButton value = entry.getValue();
                entry.setValue(template.withReplacementsFromFiller(value));
            }
        }

        // todo: actually need to be aware of inventory shape/size ...

        container.putAll(buttons);
    }

    @Nonnull
    Map<Integer, ItemButton> rawItems(PlayerContext context);

    @Nullable
    ItemButton itemTemplate();
}
