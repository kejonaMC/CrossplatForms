package dev.projectg.crossplatforms.item;

import dev.projectg.crossplatforms.config.Configuration;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.Map;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class AccessItemConfig extends Configuration {

    private transient final int defaultVersion = 1;

    private boolean enable = false;

    private boolean setHeldSlot = false;

    private final Map<AccessItem.Limit, PermissionDefault> globalPermissionDefaults = Collections.emptyMap();

    private Map<String, AccessItem> items = Collections.emptyMap();
}
