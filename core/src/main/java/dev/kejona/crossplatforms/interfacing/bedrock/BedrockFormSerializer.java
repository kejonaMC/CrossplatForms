package dev.kejona.crossplatforms.interfacing.bedrock;

import dev.kejona.crossplatforms.interfacing.bedrock.custom.CustomBedrockForm;
import dev.kejona.crossplatforms.interfacing.bedrock.modal.ModalBedrockForm;
import dev.kejona.crossplatforms.interfacing.bedrock.simple.SimpleBedrockForm;
import dev.kejona.crossplatforms.serialize.KeyedTypeSerializer;

/**
 * This serializer must be registered exact.
 */
public class BedrockFormSerializer extends KeyedTypeSerializer<BedrockForm> {

    public BedrockFormSerializer() {
        registerType(CustomBedrockForm.TYPE, CustomBedrockForm.class);
        registerType(ModalBedrockForm.TYPE, ModalBedrockForm.class);
        registerType(SimpleBedrockForm.TYPE, SimpleBedrockForm.class);
    }
}
