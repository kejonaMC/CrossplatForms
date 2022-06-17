package dev.projectg.crossplatforms.reloadable;

/**
 * Any classes that implements this interface should be able to reload their functionality.
 */
public interface Reloadable {

    /**
     * Reload the functionality of the class.
     * @return false if there was a severe error
     */
    boolean reload();
}
