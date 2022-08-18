package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.resolver.Resolver;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.InputComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.Objects;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
public class Input extends CustomComponent {

    public static final String TYPE = "input";

    private String placeholder = "";
    private String defaultText = "";

    public Input(@Nonnull String text,
                 @Nonnull String placeholder,
                 @Nonnull String defaultText) {
        super(text);
        this.placeholder = Objects.requireNonNull(placeholder);
        this.defaultText = Objects.requireNonNull(defaultText);
    }

    public Input(@Nonnull String text) {
        this(text, "", "");
    }

    @Inject
    private Input() {
        super();
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
    public void prepare(@Nonnull Resolver resolver) {
        super.prepare(resolver);
        placeholder = resolver.apply(placeholder);
        defaultText = resolver.apply(defaultText);
    }

    @Override
    public Input preparedCopy(Resolver resolver) {
        Input copy = copy();
        copy.prepare(resolver);
        return copy;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Nonnull
    @Override
    public String resultIfHidden() {
        return defaultText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Input input = (Input) o;
        return super.equals(o) && placeholder.equals(input.placeholder) && defaultText.equals(input.defaultText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeholder, defaultText);
    }

    public static Builder builder() {
        return new Builder();
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
