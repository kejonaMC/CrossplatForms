package dev.projectg.crossplatforms;

import java.util.function.Function;

/**
 * A placeholder resolver.
 */
@FunctionalInterface
public interface Resolver extends Function<String, String> {

    static Resolver identity() {
        return s -> s;
    }
}
