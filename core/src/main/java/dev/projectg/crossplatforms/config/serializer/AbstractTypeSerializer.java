package dev.projectg.crossplatforms.config.serializer;

import lombok.Getter;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Deserializes a map with keys of type identifiers (strings) and values that are instances of type provided type T,
 * into a list of T.
 * @param <E> The parent type that all entry values have in common.
 */
@Getter
public abstract class AbstractTypeSerializer<E> implements TypeSerializer<List<E>> {

    private final Map<String, Class<? extends E>> types = new HashMap<>();

    /**
     * Register a type to be deserialized.
     *
     * @param typeId The string identifier of the type, used as the map key
     * @param type   The type of the map value. Must be serializable by Configurate
     */
    public void registerType(String typeId, Class<? extends E> type) {
        if (types.get(typeId) != null) {
            throw new IllegalArgumentException("Type " + typeId + " is already registered");
        }
        types.put(typeId, type);
    }
}
