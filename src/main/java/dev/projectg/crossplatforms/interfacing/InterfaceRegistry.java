package dev.projectg.crossplatforms.interfacing;

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
public abstract class InterfaceRegistry extends Configuration {

    protected boolean enable = true;

    protected final Map<Interface.Limit, PermissionDefault> globalPermissionDefaults = Collections.emptyMap();
}
