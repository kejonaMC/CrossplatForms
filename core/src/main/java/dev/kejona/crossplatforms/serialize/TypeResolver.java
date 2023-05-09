package dev.kejona.crossplatforms.serialize;

import dev.kejona.crossplatforms.utils.ConfigurateUtils;
import org.spongepowered.configurate.ConfigurationNode;

import javax.annotation.Nullable;

@FunctionalInterface
public interface TypeResolver {

    @Nullable
    String inferType(ConfigurationNode node);

    static TypeResolver listOrScalar(String childKey, String type) {
        return node -> {
            if (ConfigurateUtils.isListOrScalar(node, childKey)) {
                return type;
            }
            return null;
        };
    }
}
