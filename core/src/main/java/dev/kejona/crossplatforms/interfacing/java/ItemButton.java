package dev.kejona.crossplatforms.interfacing.java;


import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.handler.FormPlayer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

@ToString
@NoArgsConstructor
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ItemButton {

    public static final String STATIC_IDENTIFIER = "crossplatformsbutton";

    @Nullable
    private String displayName;

    @Nullable
    private String material;

    @Nullable
    private String skullOwner;

    /**
     * Only used internally for fillers
     */
    @Nullable
    private transient FormPlayer targetPlayer;

    private OptionalInt customModelData;

    //private ConfigurationNode nbt; todo: possible support

    private List<String> lore = Collections.emptyList();

    private List<Action<? super JavaMenu>> anyClick = Collections.emptyList();
    private List<Action<? super JavaMenu>> leftClick = Collections.emptyList();
    private List<Action<? super JavaMenu>> rightClick = Collections.emptyList();

    public String getDisplayName() {
        if (displayName == null) {
            return "";
        }
        return displayName;
    }

    public boolean isPlayerHead() {
        return skullOwner != null;
    }

    public static ItemButton fillEntry(String displayName, FormPlayer skullOwner) {
        ItemButton item = new ItemButton();
        item.displayName = displayName;
        item.skullOwner = skullOwner.getName();
        item.targetPlayer = skullOwner;
        return item;
    }

    public static ItemButton fillEntry(String displayName) {
        ItemButton item = new ItemButton();
        item.displayName = displayName;
        return item;
    }
}
