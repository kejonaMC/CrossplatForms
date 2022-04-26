package dev.projectg.crossplatforms.action;

import dev.projectg.crossplatforms.interfacing.java.MenuAction;
import dev.projectg.crossplatforms.serialize.KeyedTypeListSerializer;
import dev.projectg.crossplatforms.serialize.KeyedTypeSerializer;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ActionSerializer {

    private final KeyedTypeSerializer<Action> genericActionSerializer = new KeyedTypeSerializer<>();
    private final KeyedTypeSerializer<MenuAction> menuActionSerializer = new KeyedTypeSerializer<>();

    public <V> void simpleGenericAction(String typeId, TypeToken<V> valueType, Function<V, Action> creator) {
        genericActionSerializer.registerSimpleType(typeId, valueType, creator);
        menuActionSerializer.registerSimpleType(typeId, valueType, creator::apply);
    }

    public <V> void simpleGenericAction(String typeId, Class<V> valueType, Function<V, Action> creator) {
        simpleGenericAction(typeId, TypeToken.get(valueType), creator);
    }

    public void genericAction(String typeId, Class<? extends Action> type) {
        genericActionSerializer.registerType(typeId, type);
        menuActionSerializer.registerType(typeId, type);
    }

    public <V> void simpleMenuAction(String typeId, TypeToken<V> valueType, Function<V, MenuAction> creator) {
        menuActionSerializer.registerSimpleType(typeId, valueType, creator);
    }

    public <V> void simpleMenuAction(String typeId, Class<V> valueType, Function<V, MenuAction> creator) {
        simpleMenuAction(typeId, TypeToken.get(valueType), creator);
    }

    public void menuAction(String typeId, Class<? extends MenuAction> type) {
        menuActionSerializer.registerType(typeId, type);
    }

    public Consumer<TypeSerializerCollection.Builder> registrator() {
        return builder -> {
            builder.register(new TypeToken<List<Action>>() {}, new KeyedTypeListSerializer<>(genericActionSerializer));
            builder.registerExact(new TypeToken<List<MenuAction>>() {}, new KeyedTypeListSerializer<>(menuActionSerializer));
        };
    }
}
