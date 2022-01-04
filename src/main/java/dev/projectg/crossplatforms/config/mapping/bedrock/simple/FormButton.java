package dev.projectg.crossplatforms.config.mapping.bedrock.simple;


import dev.projectg.crossplatforms.config.mapping.ClickAction;
import lombok.*;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Getter
@AllArgsConstructor
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class FormButton extends ClickAction implements ButtonComponent {

    @Nonnull
    @With
    @Required
    private String text;

    @Nullable
    @Setting("image")
    private FormImage image;
    // todo: serializer
}
