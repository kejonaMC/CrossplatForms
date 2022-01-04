package dev.projectg.crossplatforms.form;

import dev.projectg.crossplatforms.config.Configuration;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

@Getter
@ConfigSerializable
public class AccessItems extends Configuration {

    @Getter
    private final int defaultVersion = 1;

    private boolean enable;

    private Map<String, AccessItem> items;
}
