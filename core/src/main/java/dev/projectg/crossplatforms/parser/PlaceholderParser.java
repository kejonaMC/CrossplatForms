package dev.projectg.crossplatforms.parser;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PlaceholderParser implements Parser {

    public static final String TYPE = "placeholders";

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public String parse(FormPlayer player, CustomComponent component, String primitive) {
        return CrossplatForms.getInstance().getPlaceholders().setPlaceholders(player, primitive);
    }
}
