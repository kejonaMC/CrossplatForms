package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.Resolver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.cumulus.component.LabelComponent;
import org.geysermc.cumulus.util.ComponentType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.function.Function;

@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigSerializable
public class Label extends CustomComponent implements LabelComponent {
    // text is handled in Component super class (both here and in Cumulus)

    @Override
    public void setPlaceholders(@Nonnull Resolver resolver) {
        super.setPlaceholders(resolver);
    }

    @Override
    public Label copy() {
        Label label = new Label();
        copyBasics(this, label);
        return label;
    }

    @Override
    public @NonNull ComponentType getType() {
        return ComponentType.LABEL;
    }
}
