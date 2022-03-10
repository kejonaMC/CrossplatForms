package dev.projectg.crossplatforms;

import java.util.function.Function;

/**
 * A placeholder resolver.
 */
public interface Resolver extends Function<String, String> {

    static Resolver identity() {
        return IdentityResolver.INSTANCE;
    }

    class IdentityResolver implements Resolver {

        private static final IdentityResolver INSTANCE = new IdentityResolver();

        @Override
        public String apply(String s) {
            return s;
        }
    }
}
