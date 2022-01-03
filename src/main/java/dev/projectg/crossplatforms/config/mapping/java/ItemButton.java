package dev.projectg.crossplatforms.config.mapping.java;


import dev.projectg.crossplatforms.config.mapping.ClickAction;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

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
    private ClickAction anyClick;
    @Nullable
    private ClickAction leftClick;
    @Nullable
    private ClickAction rightClick;

    public boolean validate() {
        return displayName != null && material != null && !material.isEmpty();
    }
}
