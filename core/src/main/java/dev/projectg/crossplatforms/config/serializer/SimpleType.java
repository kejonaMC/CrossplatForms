package dev.projectg.crossplatforms.config.serializer;

import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.Objects;

@ToString
public abstract class SimpleType<V> implements KeyedType {

    private final String type;

    private final V value;

    public SimpleType(@Nonnull String type, @Nonnull V value) {
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public final String type() {
        return type;
    }

    @Override
    public final V value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleType<?> that = (SimpleType<?>) o;
        return type.equals(that.type) && value.equals(that.value);
    }
}
