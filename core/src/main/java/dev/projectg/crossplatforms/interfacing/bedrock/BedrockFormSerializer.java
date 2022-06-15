package dev.projectg.crossplatforms.interfacing.bedrock;

import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomBedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.modal.ModalBedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.simple.SimpleBedrockForm;
import dev.projectg.crossplatforms.serialize.ValuedTypeSerializer;

/**
 * This serializer must be registered exact.
 */
public class BedrockFormSerializer extends ValuedTypeSerializer<BedrockForm> {

    public BedrockFormSerializer() {
        registerType(CustomBedrockForm.TYPE, CustomBedrockForm.class);
        registerType(ModalBedrockForm.TYPE, ModalBedrockForm.class);
        registerType(SimpleBedrockForm.TYPE, SimpleBedrockForm.class);
    }
}
