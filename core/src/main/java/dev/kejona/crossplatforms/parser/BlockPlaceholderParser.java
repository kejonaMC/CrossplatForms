package dev.kejona.crossplatforms.parser;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.regex.Pattern;

@ConfigSerializable
public class BlockPlaceholderParser implements Parser {

    public static final String TYPE = "block-placeholders";

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%{]([^\\s]+)[%}]"); // blocks % and {} placeholders
    public static final String PLACEHOLDER_REPLACEMENT = "<blocked placeholder>";

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public String parse(FormPlayer player, CustomComponent component, String primitive) {
        return PLACEHOLDER_PATTERN.matcher(primitive).replaceAll(PLACEHOLDER_REPLACEMENT);
    }
}
