package dev.projectg.crossplatforms.interfacing.java;


import dev.projectg.crossplatforms.action.Action;
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

    public static final String STATIC_IDENTIFIER = "crossplatFormsButton";

    @Required
    private String displayName;
    @Required
    private String material;

    private List<String> lore = Collections.emptyList();

    private List<Action> anyClick = Collections.emptyList();
    private List<Action> leftClick = Collections.emptyList();
    private List<Action> rightClick = Collections.emptyList();
}
