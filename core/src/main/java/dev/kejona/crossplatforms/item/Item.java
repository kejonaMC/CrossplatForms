package dev.kejona.crossplatforms.item;

public interface Item {

    Object handle();

    @SuppressWarnings("unchecked")
    default <T> T castedHandle() {
        return (T) handle();
    }
}
