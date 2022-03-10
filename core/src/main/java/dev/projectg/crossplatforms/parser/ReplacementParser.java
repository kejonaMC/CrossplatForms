package dev.projectg.crossplatforms.parser;

import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Map;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ReplacementParser implements Parser {

    public static final String TYPE = "replacements";

    @Required
    private Map<String, String> replacements = null;

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public String parse(FormPlayer player, CustomComponent component, String primitive) {
        String result = primitive;
        for (String target : replacements.keySet()) {
            String value = replacements.get(target);
            if (value != null && !value.isEmpty()) {
                result = result.replace(target, value);
            }
        }
        return result;
    }
}
