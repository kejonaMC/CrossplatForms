package dev.projectg.crossplatforms.form.java;

import dev.projectg.crossplatforms.config.Configuration;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Map;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class MenuConfig extends Configuration {

    private final int defaultVersion = 1;

    private boolean enable = true;

    @Required
    private Map<String, JavaMenu> menus;
}
