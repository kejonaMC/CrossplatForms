package dev.kejona.crossplatforms.item;

public interface Inventory {

    Object handle();

    @SuppressWarnings("unchecked")
    default <T> T castedHandle() {
        return (T) handle();
    }

    String title();

    void setSlot(int index, Item item);
}
