package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.context.PlayerContext;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.Option;
import dev.kejona.crossplatforms.interfacing.bedrock.simple.SimpleButton;
import dev.kejona.crossplatforms.interfacing.java.ItemButton;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@Accessors(fluent = true)
@ConfigSerializable
public abstract class UniversalFiller implements OptionFiller, SimpleFormFiller, InventoryFiller {

    /**
     * OptionFormat is used here since Option is deserialized as a scalar, and the format node is a map.
     */
    @Nullable
    @Setting(value = "format")
    private OptionFormat optionTemplate;

    @Nullable
    @Setting(value = "format")
    private SimpleButton buttonTemplate;

    @SuppressWarnings("FieldMayBeFinal")
    private int insertIndex = -1;

    @Nullable
    @Setting(value = "format")
    private ItemButton itemTemplate;

    @Nullable
    @Override
    public Option optionTemplate() {
        if (optionTemplate != null) {
            return optionTemplate.text;
        }
        return null;
    }

    @Nonnull
    @Override
    public Map<Integer, ItemButton> rawItems(PlayerContext context) {
        // String[]::new takes size as input
        String[] options = rawOptions(context).toArray(String[]::new);

        Map<Integer, ItemButton> buttons = new HashMap<>(options.length);
        int i = 0;
        for (String option : options) {
            buttons.put(i, ItemButton.fillEntry(option));
            i++;
        }

        return buttons;
    }

    @Nonnull
    @Override
    public Stream<SimpleButton> rawButtons(PlayerContext context) {
        return rawOptions(context).map(SimpleButton::new);
    }

    @ConfigSerializable
    private static class OptionFormat {
        Option text;
    }
}
