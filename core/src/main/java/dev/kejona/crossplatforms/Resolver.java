package dev.projectg.crossplatforms;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * A placeholder resolver.
 */
@FunctionalInterface
public interface Resolver extends Function<String, String> {

    @Nonnull
    @Override
    String apply(@Nonnull String s);

    static Resolver identity() {
        return s -> s;
    }
}
