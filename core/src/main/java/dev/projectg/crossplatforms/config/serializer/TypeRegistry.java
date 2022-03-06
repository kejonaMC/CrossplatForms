package dev.projectg.crossplatforms.config.serializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Deserializes a map with keys of type identifiers (strings) and values that are instances of type provided type T,
 * into a list of T.
 * @param <T> The parent type that all entry values have in common.
 */
public abstract class TypeRegistry<T> {

    private final Map<String, Class<? extends T>> types = new HashMap<>();

    /**
     * Register a type to be deserialized.
     *
     * @param typeId The string identifier of the type, used as the map key
     * @param type   The type of the map value. Must be serializable by Configurate
     * @throws IllegalArgumentException If the type identifier has already been registered.
     */
    public void registerType(String typeId, Class<? extends T> type) {
        if (types.get(typeId) != null) {
            throw new IllegalArgumentException("Type " + typeId + " is already registered");
        }
        types.put(typeId, type);
    }

    /**
     * Get a type by its identifier
     * @param typeID The identifier to look up
     * @return The type, null if the type identifier is not registered.
     */
    @Nullable
    public Class<? extends T> getType(String typeID) {
        return types.get(typeID);
    }

    @Nonnull
    public Set<String> getTypes() {
        return types.keySet();
    }
}
