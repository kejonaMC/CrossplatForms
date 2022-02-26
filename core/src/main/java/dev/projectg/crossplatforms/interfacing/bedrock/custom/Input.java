package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.component.InputComponent;
import org.geysermc.cumulus.util.ComponentType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Input extends CustomComponent implements InputComponent {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%{]([^\s]+)[%}]"); // blocks % and {} placeholders
    public static final String PLACEHOLDER_REPLACEMENT = "<blocked placeholder>";
    private static final int REPLACEMENT_LENGTH = PLACEHOLDER_REPLACEMENT.length();

    private String placeholder = "";
    private String defaultText = "";

    /**
     * If true, neutralize any placeholders that are sent so that they are not resolved in any actions that use the
     * result of the input.
     */
    private boolean blockPlaceholders = true;

    public Input(@Nonnull String text, @Nonnull String placeholder, @Nonnull String defaultText, boolean blockPlaceholders) {
        super(ComponentType.INPUT, text);
        this.placeholder = placeholder;
        this.defaultText = defaultText;
        this.blockPlaceholders = blockPlaceholders;
    }

    @Override
    public CustomComponent withPlaceholders(@Nonnull Function<String, String> resolver) {
        Input input = new Input();
        input.type = this.type;
        input.text = resolver.apply(this.text);
        input.defaultText = resolver.apply(this.defaultText);
        input.placeholder = resolver.apply(this.placeholder);
        input.blockPlaceholders = this.blockPlaceholders;
        return input;
    }

    @Override
    public String parse(JsonPrimitive result) {
        if (blockPlaceholders) {
            String input = result.getAsString();
            StringBuilder builder = new StringBuilder(input);
            // Don't use StringBuilder with Matcher. Bad things happen.
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(input);
            int offset = 0; // offset required because we modify the StringBuilder while using numbers from the Matcher of its original value
            while (matcher.find()) {
                int start = matcher.start() + offset; // start of placeholder
                int end = matcher.end() + offset; // end of placeholders
                builder.replace(start, end, PLACEHOLDER_REPLACEMENT); // replace with censorship
                offset = offset + (REPLACEMENT_LENGTH - (end - start)); // update placeholder by adding new length
            }
            return builder.toString();
        } else {
            return super.parse(result);
        }
    }
}
