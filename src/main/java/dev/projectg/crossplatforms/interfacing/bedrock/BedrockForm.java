package dev.projectg.crossplatforms.interfacing.bedrock;

import dev.projectg.crossplatforms.interfacing.Interface;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.util.FormType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.UUID;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public abstract class BedrockForm extends Interface {

    protected String permissionBase = "crossplatforms.form";

    @Required
    private FormType type;

    public abstract void sendForm(@Nonnull UUID bedrockPlayer);
}
