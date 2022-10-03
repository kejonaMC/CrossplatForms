package dev.kejona.crossplatforms.utils;

import java.lang.reflect.Field;

public final class ReflectionUtils {

    private ReflectionUtils() {

    }

    public static Object get(Object instance, Field field) {
        field.setAccessible(true);
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to set " + field.getName() + " on " + instance.getClass() + " accessible", e);
        }
    }
}
