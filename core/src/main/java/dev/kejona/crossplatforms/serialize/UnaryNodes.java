package dev.kejona.crossplatforms.serialize;

import dev.kejona.crossplatforms.utils.ReflectionUtils;
import io.leangen.geantyref.GenericTypeReflector;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.meta.PostProcessor;
import org.spongepowered.configurate.serialize.SerializationException;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UnaryNodes {

    String[] nodes();

    class ProcessorFactory implements PostProcessor.Factory {

        @Override
        public @Nullable PostProcessor createProcessor(Type type) throws SerializationException {
            final UnaryNodes annotation = GenericTypeReflector.erase(type).getAnnotation(UnaryNodes.class);

            if (annotation == null) {
                return null;
            }

            final String[] nodes = annotation.nodes();
            if (nodes.length < 2) {
                throw new SerializationException("nodes in annotation has a length less than 2");
            }

            return instance -> {
                final Field[] fields = instance.getClass().getFields();
                String firstSetting = null;
                for (Field field : fields) {
                    if (arrayContains(nodes, field.getName()) && ReflectionUtils.getValue(instance, field) != null) {
                        // it is a node targeted by the annotation and a value is present
                        if (firstSetting == null) {
                            firstSetting = field.getName();
                        } else {
                            throw new SerializationException("Both '" + firstSetting + "' and '" + field.getName() + "' were provided when there should only be one of: " + Arrays.toString(nodes));
                        }
                    }
                }

                if (firstSetting == null) {
                    throw new SerializationException("One and only one of the following must be provided: " + Arrays.toString(nodes));
                }
            };
        }

        private static <T> boolean arrayContains(T[] array, @Nonnull T possibleElement) {
            for (T element : array) {
                if (possibleElement.equals(element)) {
                    return true;
                }
            }
            return false;
        }
    }
}
