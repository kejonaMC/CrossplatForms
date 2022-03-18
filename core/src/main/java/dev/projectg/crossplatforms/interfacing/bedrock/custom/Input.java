package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.Resolver;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.InputComponent;
import org.geysermc.cumulus.util.ComponentType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.Objects;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Input extends CustomComponent {

    public static final String TYPE = "input";

    private String placeholder = "";
    private String defaultText = "";

    public Input(String text, String placeholder, String defaultText) {
        super(ComponentType.INPUT, text);
        this.placeholder = Objects.requireNonNull(placeholder);
        this.defaultText = Objects.requireNonNull(defaultText);
    }

    public Input() {
        super(ComponentType.INPUT, "");
    }

    @Override
    public Input copy() {
        Input input = new Input();
        input.copyBasics(this);
        input.placeholder = this.placeholder;
        input.defaultText = this.defaultText;
        return input;
    }

    @Override
    public Component cumulusComponent() {
        return InputComponent.of(text, placeholder, defaultText);
    }

    @Override
    public void placeholders(@Nonnull Resolver resolver) {
        super.placeholders(resolver);
        placeholder = resolver.apply(placeholder);
        defaultText = resolver.apply(defaultText);
    }

    @Override
    public Input withPlaceholders(Resolver resolver) {
        Input copy = copy();
        copy.placeholders(resolver);
        return copy;
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
                && other.defaultText.equals(defaultText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeholder, defaultText);
    }

    public static class Builder {
        private String text = "";
        private String placeholder = "";
        private String defaultText = "";

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

        public Input build() {
            return new Input(text, placeholder, defaultText);
        }
    }
}
