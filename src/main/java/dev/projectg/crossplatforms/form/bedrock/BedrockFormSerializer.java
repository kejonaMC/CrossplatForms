package dev.projectg.crossplatforms.form.bedrock;

import dev.projectg.crossplatforms.form.bedrock.custom.CustomForm;
import dev.projectg.crossplatforms.form.bedrock.modal.ModalForm;
import dev.projectg.crossplatforms.form.bedrock.simple.SimpleForm;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.cumulus.util.FormType;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class BedrockFormSerializer implements TypeSerializer<BedrockForm> {

    @Override
    public BedrockForm deserialize(Type type, ConfigurationNode node) throws SerializationException {
        FormType formType = node.node("type").get(FormType.class);
        if (formType == null) {
            throw new SerializationException("Bedrock form at " + node.path() + " must contain a form type!");
        }

        return switch (formType) {
            case SIMPLE_FORM -> node.get(SimpleForm.class);
            case MODAL_FORM -> node.get(ModalForm.class);
            case CUSTOM_FORM -> node.get(CustomForm.class);
        };
    }

    @Override
    public void serialize(Type type, @Nullable BedrockForm form, ConfigurationNode node) throws SerializationException {
        if (form == null) {
            node.raw(null);
            return;
        }

        switch (form.getType()) {
            case SIMPLE_FORM:
                node.set(SimpleForm.class, form);
            case MODAL_FORM:
                node.set(ModalForm.class, form);
            case CUSTOM_FORM:
                node.set(CustomForm.class, form);
            default:
                throw new SerializationException("FormType " + form.getType() + " is not supported!");
        }
    }
}
