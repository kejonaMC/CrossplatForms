package dev.projectg.crossplatforms.config.mapping;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Map;

@Getter
@ConfigSerializable
public class AccessItems {

    private boolean enable;

    @Setting("Items")
    private Map<String, AccessItem> items;
}
