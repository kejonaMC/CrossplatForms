package dev.projectg.crossplatforms.action;


import dev.projectg.crossplatforms.serialize.SimpleType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * An essentially empty abstract class to simplify extension clauses for any classes that must extend {@link SimpleType}
 * and must implement {@link Action}
 * @param <V> The type of the value that the {@link SimpleType} stores.
 */
public abstract class SimpleAction<V> extends SimpleType<V> implements Action {

    public SimpleAction(@Nonnull String type, @NotNull V value) {
        super(type, value);
    }
}
