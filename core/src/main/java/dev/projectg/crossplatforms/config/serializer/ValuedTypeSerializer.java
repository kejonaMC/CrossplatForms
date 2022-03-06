package dev.projectg.crossplatforms.config.serializer;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Deserializes a list of {@link E} depending on a string value within the serialization of each {@link E}.
 * into a list of T.
 * @param <E> The parent type that all entry values have in common.
 */
public class ValuedTypeSerializer<E extends ValuedType> extends AbstractTypeSerializer<E> {

    @Override
    public List<E> deserialize(Type returnType, ConfigurationNode node) throws SerializationException {
        List<? extends ConfigurationNode> childList = node.childrenList();
        if (childList == null) {
            throw new SerializationException("List at " + node.path() + " is empty");
        }

        List<E> mapped = new ArrayList<>();
        for (ConfigurationNode entry : childList) {
            String typeId = entry.node(ValuedType.KEY).getString();
            if (typeId == null) {
                throw new SerializationException("Entry at " + node.path() + " does not contain a 'type' value.");
            }

            Class<? extends E> type = getTypes().get(typeId);
            if (type == null) {
                throw new SerializationException("Unsupported type (not registered) <" + (typeId + "> in " + node.path()));
            }

            mapped.add(entry.get(type));
        }

        return mapped;
    }

    @Override
    public void serialize(Type type, @Nullable List<E> list, ConfigurationNode node) throws SerializationException {
        node.raw(null);

        if (list != null) {
            for (E element : list) {
                String elementType = element.getType();
                if (elementType == null || elementType.equals("")) {
                    throw new SerializationException("Developer error: " + element + " has null or empty type field (child class of ValuedType)");
                }
                node.appendListNode().set(element);
            }
        }
    }
}
