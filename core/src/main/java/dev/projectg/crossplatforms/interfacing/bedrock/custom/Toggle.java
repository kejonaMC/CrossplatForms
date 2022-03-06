package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.Resolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.cumulus.component.ToggleComponent;
import org.geysermc.cumulus.util.ComponentType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Toggle extends CustomComponent implements ToggleComponent {

    private boolean defaultValue = false;

    @Override
    public void setPlaceholders(@Nonnull Resolver resolver) {
        super.setPlaceholders(resolver);
    }

    @Override
    public CustomComponent copy() {
        Toggle toggle = new Toggle();
        copyBasics(this, toggle);
        toggle.defaultValue = this.defaultValue;
        return toggle;
    }

    @Override
    public @NonNull ComponentType getType() {
        return ComponentType.TOGGLE;
    }
}
