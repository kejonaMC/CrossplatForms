package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.interfacing.java.ItemButton;
import dev.kejona.crossplatforms.serialize.ValuedType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface InventoryFiller extends ValuedType {

    default List<ItemButton> generateItems(Resolver resolver) {
        ItemButton format = itemFormat();
        if (format == null) {
            return rawItems(resolver).collect(Collectors.toList());
        }

        throw new IllegalStateException("Item generation not implemented yet");
        // return rawItems(resolver).map(dev.kejona.crossplatforms.spigot.item -> dev.kejona.crossplatforms.spigot.item.format(format)).collect(Collectors.toList());
    }

    @Nonnull
    Stream<ItemButton> rawItems(Resolver resolver);

    @Nullable
    ItemButton itemFormat();
}
