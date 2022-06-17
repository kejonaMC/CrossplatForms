package dev.projectg.crossplatforms.utils;

import com.google.inject.Key;
import io.leangen.geantyref.TypeToken;

public class TypeUtils {

    private TypeUtils() {

    }

    @SuppressWarnings("unchecked")
    public static <T> Key<T> keyFromToken(TypeToken<T> token) {
        return (Key<T>) Key.get(token.getType());
    }
}
