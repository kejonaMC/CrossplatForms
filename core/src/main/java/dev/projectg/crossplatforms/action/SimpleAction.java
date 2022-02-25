package dev.projectg.crossplatforms.action;

import io.leangen.geantyref.TypeToken;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.SerializationException;

import javax.annotation.Nonnull;
import java.util.function.Function;

@ToString(includeFieldNames = false)
@Getter
@ConfigSerializable
public abstract class SimpleAction<T> implements Action {

    private final T value;

    public SimpleAction(@Nonnull T value) {
        this.value = value;
    }

    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    public static class Registration<T> {
        private final TypeToken<T> type;
        private final Function<T, SimpleAction<T>> factory;

        protected SimpleAction<T> fromNode(ConfigurationNode node) throws SerializationException {
            return factory.apply(node.get(type));
        }
    }
}
