package dev.projectg.crossplatforms.form.bedrock;

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
public abstract class BedrockForm {

    @Required
    private FormType type;

    private String title = "";

    public abstract void sendForm(@Nonnull UUID bedrockPlayer);
}
