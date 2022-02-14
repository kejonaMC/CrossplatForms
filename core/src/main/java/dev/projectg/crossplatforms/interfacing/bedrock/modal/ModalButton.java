package dev.projectg.crossplatforms.interfacing.bedrock.modal;

import dev.projectg.crossplatforms.interfacing.BasicClickAction;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
public class ModalButton extends BasicClickAction {

    @Required
    private String text;
}
