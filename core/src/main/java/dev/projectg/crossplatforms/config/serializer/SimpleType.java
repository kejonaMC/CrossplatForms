package dev.projectg.crossplatforms.config.serializer;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import java.util.Objects;

@ToString
@Getter
@Accessors(fluent = true)
public abstract class SimpleType<V> implements KeyedType {

    private final String type;

    private final V value;

    public SimpleType(@Nonnull String type, @Nonnull V value) {
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleType<?> that = (SimpleType<?>) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
