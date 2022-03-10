package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.Resolver;
import dev.projectg.crossplatforms.handler.FormPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.InputComponent;
import org.geysermc.cumulus.util.ComponentType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Input extends CustomComponent {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%{]([^\\s]+)[%}]"); // blocks % and {} placeholders
    public static final String PLACEHOLDER_REPLACEMENT = "<blocked placeholder>";
    private static final int REPLACEMENT_LENGTH = PLACEHOLDER_REPLACEMENT.length();

    private String placeholder = "";
    private String defaultText = "";

    /**
     * If true, neutralize any placeholders that are sent so that they are not resolved in any actions that use the
     * result of the input.
     */
    private boolean blockPlaceholders = true;

    /**
     * Static string replacements
     */
    private Map<String, String> replacements = new HashMap<>();

    public Input(String text, String placeholder, String defaultText, boolean blockPlaceholders, Map<String, String> replacements) {
        super(ComponentType.INPUT, text);
        this.text = text;
        this.placeholder = placeholder;
        this.defaultText = defaultText;
        this.blockPlaceholders = blockPlaceholders;
        this.replacements = replacements;
    }

    @Override
    public Input copy() {
        Input input = new Input();
        input.copyBasics(this);
        input.placeholder = this.placeholder;
        input.defaultText = this.defaultText;
        input.blockPlaceholders = this.blockPlaceholders;
        input.replacements = new HashMap<>(this.replacements);
        return input;
    }

    @Override
    public Component cumulusComponent() {
        return InputComponent.of(text, placeholder, defaultText);
    }

    @Override
    public void setPlaceholders(@Nonnull Resolver resolver) {
        super.setPlaceholders(resolver);
        placeholder = resolver.apply(placeholder);
        defaultText = resolver.apply(defaultText);
        replacements = replacements.keySet().stream().collect(Collectors.toMap(Function.identity(), resolver));
    }

    @Override
    public Input withPlaceholders(Resolver resolver) {
        Input copy = copy();
        copy.setPlaceholders(resolver);
        return copy;
    }

    @Override
    public String parse(FormPlayer player, String result) {
        String parsed = result;

        for (String target : replacements.keySet()) {
            parsed = parsed.replace(target, replacements.get(target));
        }

        if (blockPlaceholders) {
            StringBuilder builder = new StringBuilder(parsed);
            // Don't use StringBuilder with Matcher, since SB is mutable. Makes things even more difficult.
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(parsed);
            int offset = 0; // offset required because we modify the StringBuilder while using numbers from the Matcher of its original value
            while (matcher.find()) {
                int start = matcher.start() + offset; // start of placeholder
                int end = matcher.end() + offset; // end of placeholders
                builder.replace(start, end, PLACEHOLDER_REPLACEMENT); // replace with censorship
                offset = offset + (REPLACEMENT_LENGTH - (end - start)); // update offset by adding new length
            }
            parsed = builder.toString();
        }

        return super.parse(player, parsed);
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o.getClass().equals(getClass()))) return false;
        final Input other = (Input) o;
        return other.type.equals(type)
                && other.text.equals(text)
                && other.placeholder.equals(placeholder)
                && other.defaultText.equals(defaultText)
                && other.blockPlaceholders == blockPlaceholders && other.replacements.equals(replacements);
    }

    public static class Builder {
        private String text = "";
        private String placeholder = "";
        private String defaultText = "";
        private boolean blockPlaceholders = true;
        private Map<String, String> replacements = new HashMap<>();

        public Builder text(String text) {
            this.text = text;
            return this;
        }
        public Builder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }
        public Builder defaultText(String defaultText) {
            this.defaultText = defaultText;
            return this;
        }
        public Builder blockPlaceholders(boolean blockPlaceholders) {
            this.blockPlaceholders = blockPlaceholders;
            return this;
        }
        public Builder replacements(Map<String, String> replacements) {
            this.replacements = replacements;
            return this;
        }

        public Input build() {
            return new Input(text, placeholder, defaultText, blockPlaceholders, replacements);
        }
    }
}
