package dev.kejona.crossplatforms.serialize;

import lombok.AllArgsConstructor;
import lombok.ToString;

import javax.annotation.Nonnull;

@ToString
@AllArgsConstructor
public abstract class SimpleType<V> implements KeyedType {

    @Nonnull
    private final String type;

    @Nonnull
    private final V value;

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
