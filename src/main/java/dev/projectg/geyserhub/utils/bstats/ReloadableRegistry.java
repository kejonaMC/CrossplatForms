package dev.projectg.geyserhub.utils.bstats;

import javax.annotation.Nonnull;
import java.util.HashSet;

public class ReloadableRegistry {

    /**
     * A set of instances that implement the Reloadable interface
     */
    private static final HashSet<Reloadable> reloadables = new HashSet<>();

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
