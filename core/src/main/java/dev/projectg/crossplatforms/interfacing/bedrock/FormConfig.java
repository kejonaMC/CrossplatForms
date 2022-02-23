package dev.projectg.crossplatforms.interfacing.bedrock;

import dev.projectg.crossplatforms.interfacing.InterfaceConfig;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.Map;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class FormConfig extends InterfaceConfig {

    public static final int VERSION = 2;
    public static final int MINIMUM_VERSION = 2; //todo

    private Map<String, BedrockForm> forms = Collections.emptyMap();
}
