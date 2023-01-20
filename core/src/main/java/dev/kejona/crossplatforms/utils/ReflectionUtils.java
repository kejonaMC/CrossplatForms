package dev.kejona.crossplatforms.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public final class ReflectionUtils {

    private ReflectionUtils() {

    }

    @Nullable
    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Nonnull
    public static Class<?> requireClass(String name) {
        return Objects.requireNonNull(getClass(name), "Class for name " + name);
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

    @Nonnull
    public static Method requireMethod(Class<?> clazz, String methodName, Class<?>... arguments) {
        return Objects.requireNonNull(
            getMethod(clazz, methodName, arguments),
            () -> methodName + " method of " + clazz.getName() + " with arguments " + Arrays.toString(arguments)
        );
    }

    @Nullable
    public static Object invoke(Object instance, Method method, Object... arguments) {
        method.setAccessible(true);
        try {
            return method.invoke(instance, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
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
    public static Field getField(Class<?> clazz, String name) {
        Field field = getField(clazz, name, true); // try declared fields, private or public
        if (field == null) {
            field = getField(clazz, name, false); // try public inherited fields
        }

        return field;
    }

    @Nonnull
    public static Field requireField(Class<?> clazz, String name) {
        return Objects.requireNonNull(getField(clazz, name), () -> name + " field of " + clazz.getName());
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
}
