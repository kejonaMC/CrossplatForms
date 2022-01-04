package dev.projectg.crossplatforms.config.mapping.bedrock;

import lombok.Getter;
import org.geysermc.cumulus.util.FormType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.UUID;

@Getter
@ConfigSerializable
public abstract class BedrockForm {

    @Required
    private FormType type;

    public abstract void sendForm(@Nonnull UUID bedrockPlayer);
}
