package dev.kejona.crossplatforms.serialize;

import org.spongepowered.configurate.serialize.SerializationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.leangen.geantyref.GenericTypeReflector.isSuperType;

/**
 * Deserializes a map with keys of type identifiers (strings) and values that are instances of type provided type T,
 * into a list of T.
 * @param <T> The parent type that all entry values have in common.
 */
public class TypeRegistry<T> {

    private final Map<String, Class<? extends T>> types = new HashMap<>();

    /**
     * Register a type to be deserialized.
     *
     * @param typeId The string identifier of the type, used as the map key
     * @param type   The type of the map value. Must be serializable by Configurate
     * @throws IllegalArgumentException If the type identifier has already been registered.
     */
    public void registerType(String typeId, Class<? extends T> type) {
        String lowerCase = typeId.toLowerCase(Locale.ROOT);
        if (types.get(lowerCase) != null) {
            throw new IllegalArgumentException("Type " + lowerCase + " is already registered");
        }
        types.put(lowerCase, type);
    }

    /**
     * Get a type by its identifier
     * @param typeId The identifier to look up
     * @return The type, null if the type identifier is not registered.
     */
    @Nullable
    public Class<? extends T> getType(String typeId) {
        return types.get(typeId.toLowerCase(Locale.ROOT));
    }

    @Nonnull
    public Set<String> getTypes(Type superType) {
        return filter(superType, types);
    }

    public void validateType(Type required, String typeKey, Type registered) throws SerializationException {
        if (!isValidType(required, registered)) {
            throw new SerializationException("Unsupported type '" + typeKey + "'. " + registered + " is registered but incompatible. Possible type options are: " + getTypes(required));
        }
    }

    public boolean isValidType(Type required, Type registered) {
        return isSuperType(required, registered);
    }

    public final <V> Set<String> filter(Type superType, Map<String, Class<? extends V>> types) {
        return filter(superType, types.entrySet().stream());
    }

    public final <V> Set<String> filter(Type superType, Stream<Map.Entry<String, Class<? extends V>>> types) {
        return types.filter(e -> isValidType(superType, e.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }
}
