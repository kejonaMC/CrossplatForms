package dev.projectg.crossplatforms.config.serializer;

import lombok.ToString;

import javax.annotation.Nonnull;

@ToString(includeFieldNames = false)
public abstract class SimpleType<V> implements KeyedType {

    private final V value;

    public SimpleType(@Nonnull V value) {
        this.value = value;
    }

    @Override
    public V value() {
        return value;
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
