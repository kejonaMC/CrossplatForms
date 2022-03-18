package dev.projectg.crossplatforms.parser;

import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ReplacementParser implements Parser {

    public static final String TYPE = "replacements";

    @Required
    private Map<String, String> replacements = new HashMap<>(0);

    @SuppressWarnings("unused")
    private ReplacementParser() {

    }

    public ReplacementParser(Map<String, String> replacements) {
        this.replacements = replacements;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public String parse(FormPlayer player, CustomComponent component, String primitive) {
        String result = primitive;
        for (String target : replacements.keySet()) {
            String value = replacements.get(target);
            value = (value == null) ? "" : value;
            result = result.replace(target, value);
        }
        return result;
    }
}
