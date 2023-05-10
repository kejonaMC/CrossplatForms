package dev.kejona.crossplatforms.serialize;

import io.leangen.geantyref.GenericTypeReflector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (types.containsKey(lowerCase)) {
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
        return types.entrySet().stream()
            .filter(e -> isCompatible(superType, e.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    /**
     * Tests if a registered Type is compatible with some required Type.
     * @param required the Type that is required. For example, in the context of serialization, this would be the declared field type.
     * @param registered a Type that is registered in a Registry
     * @return true if the registered type is a subtype of the required type
     */
    public static boolean isCompatible(Type required, Type registered) {
        return GenericTypeReflector.isSuperType(required, registered);
    }
}
