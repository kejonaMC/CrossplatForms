package dev.kejona.crossplatforms.proxy.item;

import dev.kejona.crossplatforms.item.Item;
import dev.simplix.protocolize.api.item.BaseItemStack;
import lombok.RequiredArgsConstructor;
import net.querz.nbt.tag.CompoundTag;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

@RequiredArgsConstructor
public class ProtocolizeItem implements Item {

    private static final String CUSTOM_MODEL_DATA_KEY = "CustomModelData";

    private final BaseItemStack item;

    @Override
    public Object handle() {
        return item;
    }

    @Override
    public void displayName(@Nonnull String name) {
        item.displayName(Objects.requireNonNull(name, "name"));
    }

    @Override
    public void lore(@Nonnull List<String> lore) {
        item.lore(lore, true);
    }

    @Override
    public void customModelData(OptionalInt id) {
        CompoundTag nbt = item.nbtData();

        if (id.isPresent()) {
            nbt.putInt(CUSTOM_MODEL_DATA_KEY, id.getAsInt());
        } else {
            nbt.remove(CUSTOM_MODEL_DATA_KEY);
        }
    }
}
