package dev.kejona.crossplatforms.serialize;

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeFactory;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamSerializer implements TypeSerializer.Annotated<Stream<?>> {

    public static final TypeToken<Stream<?>> TYPE = new TypeToken<Stream<?>>() {};

    @Override
    public Stream<?> deserialize(AnnotatedType type, ConfigurationNode node) throws SerializationException {
        final AnnotatedType elementType = elementType(type);

        if (node.isList()) {
            if (node.childrenList().size() == 0) {
                return Stream.of();
            }

            final List<?> list = (List<?>) node.get(listType(elementType));
            if (list == null) {
                throw new SerializationException("Deserialization of node to list resulted in null");
            }

            return list.stream();
        }
        if (node.isMap()) {
            if (GenericTypeReflector.isSuperType(Map.Entry.class, elementType.getType())) {
                if (node.childrenMap().size() == 0) {
                    return Stream.of();
                }

                final Map<?, ?> map = (Map<?, ?>) node.get(mapType(elementType));
                if (map == null) {
                    throw new SerializationException("Deserialization of node to map resulted in null");
                }
                return map.entrySet().stream();
            } else {
                throw new SerializationException("Maps are not supported for this stream type. Try using a list or a single value.");
            }
        }

        final Object single = node.get(elementType);
        if (single == null) {
            throw new SerializationException("Deserialization of node to " + elementType.getType().getTypeName() + " resulted in null");
        }
        return Stream.of(single);
    }

    @SuppressWarnings("unchecked") // casting to Map.Entry stream should be safe because of isSuperType check
    @Override
    public void serialize(AnnotatedType type, @Nullable Stream<?> stream, ConfigurationNode node) throws SerializationException {
        if (stream == null) {
            node.raw(null);
            return;
        }

        final AnnotatedType elementType = elementType(type);
        if (GenericTypeReflector.isSuperType(Map.Entry.class, elementType.getType())) {
            final Stream<Map.Entry<?, ?>> source = ((Stream<Map.Entry<?,?>>) stream);
            final Map<?, ?> map = source.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            node.set(mapType(elementType), map);
        } else {
            final List<?> list = stream.collect(Collectors.toList());
            node.set(listType(elementType), list);
        }
    }

    private static AnnotatedType elementType(final AnnotatedType containerType) throws SerializationException {
        if (!(containerType instanceof AnnotatedParameterizedType)) {
            throw new SerializationException(containerType, "Raw types are not supported for streams");
        }
        return ((AnnotatedParameterizedType) containerType).getAnnotatedActualTypeArguments()[0];
    }

    private static Type listType(AnnotatedType elementType) {
        return TypeFactory.parameterizedClass(List.class, elementType.getType());
    }

    private static Type mapType(AnnotatedType mapEntryType) throws SerializationException {
        if (!(mapEntryType instanceof AnnotatedParameterizedType)) {
            throw new SerializationException(mapEntryType, "Raw map entry types are not supported for streams");
        }
        AnnotatedType[] parameters = ((AnnotatedParameterizedType) mapEntryType).getAnnotatedActualTypeArguments();
        return TypeFactory.parameterizedClass(Map.class, parameters[0].getType(), parameters[1].getType());
    }
}
