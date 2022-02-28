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
}
