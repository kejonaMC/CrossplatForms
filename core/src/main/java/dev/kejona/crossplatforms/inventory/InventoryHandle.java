package dev.kejona.crossplatforms.inventory;

public interface InventoryHandle {

    Object handle();

    @SuppressWarnings({"unchecked", "unused"})
    default <T> T castedHandle(Class<T> type) {
        return (T) handle();
    }

    String title();

    void setSlot(int index, ItemHandle item);
}
