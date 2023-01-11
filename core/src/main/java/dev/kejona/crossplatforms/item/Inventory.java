package dev.kejona.crossplatforms.item;

public interface Inventory {

    Object handle();

    @SuppressWarnings("unchecked")
    default <T> T castedHandle() {
        return (T) handle();
    }

    void setSlot(int index, Item item);
}
