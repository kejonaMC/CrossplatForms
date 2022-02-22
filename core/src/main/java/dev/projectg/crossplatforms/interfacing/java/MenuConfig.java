package dev.projectg.crossplatforms.interfacing.java;

import dev.projectg.crossplatforms.interfacing.InterfaceConfig;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.Map;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class MenuConfig extends InterfaceConfig {

    public static final int VERSION = 2;
    public static final int MINIMUM_VERSION = 2; //todo: update

    private Map<String, JavaMenu> menus = Collections.emptyMap();
}