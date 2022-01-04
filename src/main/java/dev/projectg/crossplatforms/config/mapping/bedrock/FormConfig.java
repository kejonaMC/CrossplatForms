package dev.projectg.crossplatforms.config.mapping.bedrock;

import dev.projectg.crossplatforms.config.mapping.InterfaceCollection;
import lombok.Getter;

public class FormConfig extends InterfaceCollection<BedrockForm> {

    @Getter
    private static final int defaultVersion = 1;
}
