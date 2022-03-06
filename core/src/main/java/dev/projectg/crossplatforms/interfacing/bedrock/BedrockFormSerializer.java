package dev.projectg.crossplatforms.interfacing.bedrock;

import dev.projectg.crossplatforms.config.serializer.ValuedTypeSerializer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomForm;
import dev.projectg.crossplatforms.interfacing.bedrock.modal.ModalForm;
import dev.projectg.crossplatforms.interfacing.bedrock.simple.SimpleForm;

/**
 * This serializer must be registered exact.
 */
public class BedrockFormSerializer extends ValuedTypeSerializer<BedrockForm> {

    public BedrockFormSerializer() {
        registerType(CustomForm.TYPE, CustomForm.class);
        registerType(ModalForm.TYPE, ModalForm.class);
        registerType(SimpleForm.TYPE, SimpleForm.class);
    }
}
