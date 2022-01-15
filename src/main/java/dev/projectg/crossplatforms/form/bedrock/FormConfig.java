package dev.projectg.crossplatforms.form.bedrock;

import dev.projectg.crossplatforms.config.Configuration;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.Map;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class FormConfig extends Configuration {

    private transient final int defaultVersion = 1;

    private boolean enable = true;

    private Map<String, BedrockForm> forms = Collections.emptyMap();
}
