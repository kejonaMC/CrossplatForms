package dev.kejona.crossplatforms.interfacing.java;


import dev.kejona.crossplatforms.handler.FormPlayer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;

import static dev.kejona.crossplatforms.filler.FillerUtils.replace;

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

    //private Integer customModelData;
    //private ConfigurationNode nbt;

    private List<String> lore = Collections.emptyList();

    private List<MenuAction> anyClick = Collections.emptyList();
    private List<MenuAction> leftClick = Collections.emptyList();
    private List<MenuAction> rightClick = Collections.emptyList();

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

    public String getDisplayName() {
        if (displayName == null) {
            return "";
        }
        return displayName;
    }

    public boolean isPlayerHead() {
        return skullOwner != null;
    }

    @Contract
    public ItemButton format(ItemButton format) {
        ItemButton item = new ItemButton();
        item.displayName = replace(format.displayName, this.displayName);
        item.material = replace(format.material, this.material);

        if (this.targetPlayer != null) {
            // from filler
            item.skullOwner = this.skullOwner;
            item.targetPlayer = this.targetPlayer;
        } else {
            // from config
            item.skullOwner = this.skullOwner;
        }

        // we don't offer filling this stuff (yet at least )
        item.lore = format.lore;
        item.anyClick = format.anyClick;
        item.leftClick = format.leftClick;
        item.rightClick = format.rightClick;
        return item;
    }
}
