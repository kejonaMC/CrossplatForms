package dev.kejona.crossplatforms.inventory;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.resolver.Resolver;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;

@Getter
@ConfigSerializable
public class ConfiguredItem {

    @Inject
    protected transient InventoryFactory factory;

    protected String material;

    @Nullable
    protected String displayName;

    protected List<String> lore = Collections.emptyList();

    @Nullable
    protected Integer customModelData;

    @Nullable
    protected SkullProfile skull;

    @Inject
    protected ConfiguredItem() {

    }

    public ItemHandle convertAndResolve(Resolver resolver) {
        List<String> lore = resolver.apply(this.lore);

        if (skull == null) {
            String displayName = resolver.applyOrElse(this.displayName, "");
            String material = resolver.apply(this.material);
            return factory.item(material, displayName, lore, customModelData);
        } else {
            return factory.skullItem(skull, resolver.apply(displayName), lore);
        }
    }
}
