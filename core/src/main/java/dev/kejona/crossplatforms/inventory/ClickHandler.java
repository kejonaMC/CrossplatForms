package dev.kejona.crossplatforms.inventory;

/**
 * Responsible for handling clicks inside an inventory
 */
@FunctionalInterface
public interface ClickHandler {

    void handle(int slot, boolean rightClick);
}
