package dev.projectg.crossplatforms.action;

import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

@ToString(includeFieldNames = false)
@Getter
public class SimpleType<V> {

    private final V value;

    public SimpleType(@Nonnull V value) {
        this.value = value;
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
