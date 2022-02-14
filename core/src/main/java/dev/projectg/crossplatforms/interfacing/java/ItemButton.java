package dev.projectg.crossplatforms.interfacing.java;


import dev.projectg.crossplatforms.interfacing.BasicClickAction;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ItemButton {

    @Required
    private String displayName;
    @Required
    private String material;

    private List<String> lore = Collections.emptyList();

    @Nullable
    private BasicClickAction anyClick;
    @Nullable
    private BasicClickAction leftClick;
    @Nullable
    private BasicClickAction rightClick;
}
