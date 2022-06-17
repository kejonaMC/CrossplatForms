package dev.kejona.crossplatforms.interfacing.java;


import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Collections;
import java.util.List;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ItemButton {

    public static final String STATIC_IDENTIFIER = "crossplatformsbutton";

    @Required
    private String displayName;

    @Required
    private String material;

    private List<String> lore = Collections.emptyList();

    private List<MenuAction> anyClick = Collections.emptyList();
    private List<MenuAction> leftClick = Collections.emptyList();
    private List<MenuAction> rightClick = Collections.emptyList();
}
