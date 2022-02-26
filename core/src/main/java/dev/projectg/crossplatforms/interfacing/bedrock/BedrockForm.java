package dev.projectg.crossplatforms.interfacing.bedrock;

import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.interfacing.Interface;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.util.FormType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public abstract class BedrockForm extends Interface {

    protected transient final String permissionBase = Constants.ID + ".form.";

    @Required
    private FormType type;
}
