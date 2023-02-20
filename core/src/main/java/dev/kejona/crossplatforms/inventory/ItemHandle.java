package dev.kejona.crossplatforms.inventory;

public interface ItemHandle {

    Object handle();

    @SuppressWarnings("unchecked")
    default <T> T castedHandle() {
        return (T) handle();
    }
}
