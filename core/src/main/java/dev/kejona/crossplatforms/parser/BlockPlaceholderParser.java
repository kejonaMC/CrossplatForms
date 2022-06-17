package dev.kejona.crossplatforms.parser;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.CustomComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ConfigSerializable
public class BlockPlaceholderParser implements Parser {

    public static final String TYPE = "block-placeholders";

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%{]([^\\s]+)[%}]"); // blocks % and {} placeholders
    public static final String PLACEHOLDER_REPLACEMENT = "<blocked placeholder>";
    private static final int REPLACEMENT_LENGTH = PLACEHOLDER_REPLACEMENT.length();

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public String parse(FormPlayer player, CustomComponent component, String primitive) {
        StringBuilder builder = new StringBuilder(primitive);
        // Don't use StringBuilder with Matcher, since SB is mutable. Makes things even more difficult.
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(primitive);
        int offset = 0; // offset required because we modify the StringBuilder while using numbers from the Matcher of its original value
        while (matcher.find()) {
            int start = matcher.start() + offset; // start of placeholder
            int end = matcher.end() + offset; // end of placeholders
            builder.replace(start, end, PLACEHOLDER_REPLACEMENT); // replace with censorship
            offset = offset + (REPLACEMENT_LENGTH - (end - start)); // update offset by adding new length
        }

        return builder.toString();
    }
}
