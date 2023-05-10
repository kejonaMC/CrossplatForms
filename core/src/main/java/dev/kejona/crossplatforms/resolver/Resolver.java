package dev.kejona.crossplatforms.resolver;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * A placeholder resolver for a given player
 */
@FunctionalInterface
public interface Resolver extends UnaryOperator<String> {

    @Override
    @Contract("!null -> !null; null -> null")
    String apply(@Nullable String s);

    @Contract("null, _ -> param2")
    default String applyOrElse(@Nullable String s, @Nullable String def) {
        if (s == null) {
            return def;
        }

        return apply(s);
    }

    @Contract("!null -> !null; null -> null")
    default List<String> apply(@Nullable List<String> list) {
        if (list == null) {
            return null;
        }

        List<String> resolved = new ArrayList<>();
        for (String s : list) {
            resolved.add(apply(s));
        }
        return resolved;
    }

    default Resolver then(Resolver resolver) {
        return s -> resolver.apply(this.apply(s));
    }

    static Resolver of(@Nonnull final UnaryOperator<String> keyMapper) {
        return keyMapper::apply;
    }
}
