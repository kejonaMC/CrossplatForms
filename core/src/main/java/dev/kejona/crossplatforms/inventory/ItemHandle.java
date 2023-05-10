package dev.kejona.crossplatforms.inventory;

public interface ItemHandle {

    Object handle();

    @SuppressWarnings({"unchecked", "unused"})
    default <T> T castedHandle(Class<T> type) {
        return (T) handle();
    }
}
