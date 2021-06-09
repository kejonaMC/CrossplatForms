package dev.projectg.geyserhub;

import dev.projectg.geyserhub.Reloadable;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class ReloadableRegistry {

    /**
     * A set of instances that implement the Reloadable interface
     */
    private static final Set<Reloadable> reloadables = new HashSet<>();

    /**
     * Register a reloadable
     * @param reloadable the reloadable
     */
    public static void registerReloadable(@Nonnull Reloadable reloadable) {
        reloadables.add(reloadable);
    }

    /**
     * @return A copy of all registered reloadables
     */
    public static Reloadable[] getRegisteredReloadables() {
        return reloadables.toArray(new Reloadable[0]);
    }
}
