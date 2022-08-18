package dev.kejona.crossplatforms.interfacing.bedrock.modal;

import dev.kejona.crossplatforms.action.Action;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Collections;
import java.util.List;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ModalButton {

    @Required
    private String text = null;

    private List<Action<? super ModalBedrockForm>> actions = Collections.emptyList();
}
