package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.Resolver;
import dev.kejona.crossplatforms.interfacing.bedrock.simple.SimpleButton;
import dev.kejona.crossplatforms.interfacing.java.ItemButton;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

@Getter
@Accessors(fluent = true)
@ConfigSerializable
public abstract class UniversalFiller implements OptionFiller, SimpleFormFiller, InventoryFiller {

    @Nullable
    @Setting(value = "format")
    private String dropdownFormat;

    @Nullable
    @Setting(value = "format")
    private SimpleButton buttonFormat;

    @SuppressWarnings("FieldMayBeFinal")
    private int insertIndex = -1;

    @Nullable
    @Setting(value = "format")
    private ItemButton itemFormat;

    @Nonnull
    @Override
    public Stream<ItemButton> rawItems(Resolver resolver) {
        return rawOptions(resolver).map(ItemButton::fillEntry);
    }

    @Nonnull
    @Override
    public Stream<SimpleButton> rawButtons(Resolver resolver) {
        return rawOptions(resolver).map(SimpleButton::new);
    }
}
