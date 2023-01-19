package dev.kejona.crossplatforms.utils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionUtils {

    private ReflectionUtils() {

    }

    @Nullable
    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static Method getMethod(Class<?> clazz, String method, boolean declared, Class<?>... arguments) {
        try {
            if (declared) {
                return clazz.getDeclaredMethod(method, arguments);
            }
            return clazz.getMethod(method, arguments);
        } catch (NoSuchMethodException exception) {
            return null;
        }
    }

    @Nullable
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... arguments) {
        Method method = getMethod(clazz, methodName, true, arguments);
        if (method != null) {
            return method;
        }
        return getMethod(clazz, methodName, false, arguments);
    }

    @Nullable
    public static Object invoke(Object instance, Method method, Object... arguments) {
        method.setAccessible(true);
        try {
            return method.invoke(instance, arguments);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T castedInvoke(Object instance, Method method, Object... arguments) {
        return (T) invoke(instance, method, arguments);
    }

    @Nullable
    public static Field getField(Class<?> clazz, String name, boolean declared) {
        try {
            if (declared) {
                return clazz.getDeclaredField(name);
            } else {
                return clazz.getField(name);
            }
        } catch (NoSuchFieldException ignored) {
            return null;
        }
    }

    @Nullable
    public static Object getValue(Object instance, Field field) {
        field.setAccessible(true);
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to set " + field.getName() + " on " + instance.getClass() + " accessible", e);
        }
    }

    public static void setValue(Object instance, Field field, Object value) {
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getValue(Object instance, Field field, Class<T> type) {
        return (T) getValue(instance, field);
    }
}
