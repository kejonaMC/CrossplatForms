package dev.projectg.crossplatforms.interfacing.bedrock;

import dev.projectg.crossplatforms.interfacing.InterfaceRegistry;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.Map;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class FormConfig extends InterfaceRegistry {

    private transient final int defaultVersion = 1;

    private Map<String, BedrockForm> forms = Collections.emptyMap();
}
