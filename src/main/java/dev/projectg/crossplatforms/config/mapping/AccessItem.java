package dev.projectg.crossplatforms.config.mapping;

import dev.projectg.crossplatforms.Platform;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;

@Getter
@ConfigSerializable
public class AccessItem {

    private String material;
    private String name;
    private List<String> lore;
    private int slot;
    private String form;
    private Platform platform;
    private boolean join;

    @Setting("Allow-Drop")
    private boolean allowDrop;

    @Setting("Destroy-Dropped")
    private boolean destroyDropped;

    @Setting("Allow-Move")
    private boolean allowMove;
}
