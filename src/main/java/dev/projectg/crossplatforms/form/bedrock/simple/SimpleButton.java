package dev.projectg.crossplatforms.form.bedrock.simple;


import dev.projectg.crossplatforms.form.ClickAction;
import lombok.*;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SimpleButton extends ClickAction implements ButtonComponent {

    @With
    @Required
    private String text;

    @Nullable
    @Setting("image")
    private FormImage image;
}
