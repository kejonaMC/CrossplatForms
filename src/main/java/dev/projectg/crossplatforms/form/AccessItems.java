package dev.projectg.crossplatforms.form;

import dev.projectg.crossplatforms.config.Configuration;
import dev.projectg.crossplatforms.permission.DefaultPermission;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.Map;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class AccessItems extends Configuration {

    @Getter
    private final int defaultVersion = 1;

    private boolean enable = true;

    private boolean setHeldSlot = false;

    private DefaultPermission globalDefaultPermission = DefaultPermission.TRUE;

    private Map<String, AccessItem> items = Collections.emptyMap();
}
