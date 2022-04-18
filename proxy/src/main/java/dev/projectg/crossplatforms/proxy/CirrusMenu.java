package dev.projectg.crossplatforms.proxy;

import dev.simplix.cirrus.common.configuration.MenuConfiguration;
import dev.simplix.cirrus.common.i18n.LocalizedItemStackModel;
import dev.simplix.cirrus.common.i18n.LocalizedString;

import java.util.Map;

public class CirrusMenu implements MenuConfiguration {

    @Override
    public LocalizedString title() {
        return null;
    }

    @Override
    public dev.simplix.protocolize.data.inventory.InventoryType type() {
        return null;
    }

    @Override
    public LocalizedItemStackModel placeholderItem() {
        return null;
    }

    @Override
    public int[] reservedSlots() {
        return new int[0];
    }

    @Override
    public LocalizedItemStackModel[] items() {
        return new LocalizedItemStackModel[0];
    }

    @Override
    public Map<String, LocalizedItemStackModel> businessItems() {
        return null;
    }

    @Override
    public MenuConfiguration title(LocalizedString localizedString) {
        return null;
    }

    @Override
    public MenuConfiguration type(dev.simplix.protocolize.data.inventory.InventoryType inventoryType) {
        return null;
    }

    @Override
    public MenuConfiguration placeholderItem(LocalizedItemStackModel localizedItemStackModel) {
        return null;
    }

    @Override
    public MenuConfiguration reservedSlots(int[] ints) {
        return null;
    }

    @Override
    public MenuConfiguration items(LocalizedItemStackModel[] localizedItemStackModels) {
        return null;
    }

    @Override
    public MenuConfiguration businessItems(Map<String, LocalizedItemStackModel> map) {
        return null;
    }
}
