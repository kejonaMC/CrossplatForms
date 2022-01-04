package dev.projectg.crossplatforms.form.bedrock.modal;

import dev.projectg.crossplatforms.form.ClickAction;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

@Getter
@ConfigSerializable
public class ModalButton extends ClickAction {

    @Required
    private String text;
}
