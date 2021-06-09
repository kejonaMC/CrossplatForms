package dev.projectg.geyserhub;

/**
 * Any classes that implements this interface should be able to reload their functionality.
 */
public interface Reloadable {

    /**
     * Reload the functionality of the class.
     * @return true if the reload was successful
     */
    boolean reload();
}
