package dev.kejona.crossplatforms.interfacing.java;


import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.item.Item;
import dev.kejona.crossplatforms.item.SkullProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

@ToString
@NoArgsConstructor
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ItemButton {

    public static final String STATIC_IDENTIFIER = "crossplatformsbutton";

    @Nullable
    private String material;

    @Nullable
    private String displayName;

    private List<String> lore = Collections.emptyList();

    @Nullable
    private Integer customModelData;

    @Nullable
    private SkullProfile skull;

    //private ConfigurationNode nbt; todo: possible support

    private List<Action<? super JavaMenu>> anyClick = Collections.emptyList();
    private List<Action<? super JavaMenu>> leftClick = Collections.emptyList();
    private List<Action<? super JavaMenu>> rightClick = Collections.emptyList();

    public String getDisplayName() {
        if (displayName == null) {
            return "";
        }
        return displayName;
    }

    @Contract("_ -> new")
    public ItemButton withReplacementsFromFiller(@Nonnull ItemButton generated) {
        throw new AssertionError("Not yet implemented"); // todo: implement formatting for item button
    }

    public static ItemButton fillEntry(String displayName) {
        ItemButton item = new ItemButton();
        item.displayName = displayName;
        return item;
    }
}
