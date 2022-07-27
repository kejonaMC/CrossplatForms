package dev.kejona.crossplatforms.serialize;

import org.spongepowered.configurate.ConfigurationNode;

import javax.annotation.Nullable;

@FunctionalInterface
public interface TypeResolver {

    @Nullable
    String getType(ConfigurationNode node);
}
