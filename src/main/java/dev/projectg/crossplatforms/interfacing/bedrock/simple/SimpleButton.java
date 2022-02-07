package dev.projectg.crossplatforms.interfacing.bedrock.simple;


import dev.projectg.crossplatforms.interfacing.BasicClickAction;
import lombok.*;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nullable;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SimpleButton extends BasicClickAction implements ButtonComponent {

    @With
    @Required
    private String text;

    @Nullable
    private FormImage image;
}
