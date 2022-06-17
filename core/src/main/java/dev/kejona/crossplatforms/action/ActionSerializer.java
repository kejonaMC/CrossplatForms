package dev.kejona.crossplatforms.action;

import com.google.inject.Injector;
import dev.kejona.crossplatforms.interfacing.java.MenuAction;
import dev.kejona.crossplatforms.serialize.KeyedTypeListSerializer;
import dev.kejona.crossplatforms.serialize.KeyedTypeSerializer;
import dev.kejona.crossplatforms.serialize.SimpleType;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.List;
import java.util.function.Consumer;

public class ActionSerializer {

    private final KeyedTypeSerializer<Action> genericActionSerializer;
    private final KeyedTypeSerializer<MenuAction> menuActionSerializer;

    public ActionSerializer(Injector injector) {
        genericActionSerializer = new KeyedTypeSerializer<>(injector);
        menuActionSerializer = new KeyedTypeSerializer<>(injector);
    }

    public <V, T extends SimpleType<V> & Action> void simpleGenericAction(String typeId, TypeToken<V> valueType, Class<? extends T> actionType) {
        genericActionSerializer.registerSimpleType(typeId, valueType, actionType);
        menuActionSerializer.registerSimpleType(typeId, valueType, actionType);
    }

    public <V, T extends SimpleType<V> & Action> void simpleGenericAction(String typeId, Class<V> valueType, Class<? extends T> actionType) {
        simpleGenericAction(typeId, TypeToken.get(valueType), actionType);
    }

    public void genericAction(String typeId, Class<? extends Action> type) {
        genericActionSerializer.registerType(typeId, type);
        menuActionSerializer.registerType(typeId, type);
    }

    public <V, T extends SimpleType<V> & MenuAction> void simpleMenuAction(String typeId, TypeToken<V> valueType, Class<? extends T> actionType) {
        menuActionSerializer.registerSimpleType(typeId, valueType, actionType);
    }

    public <V, T extends SimpleType<V> & MenuAction> void simpleMenuAction(String typeId, Class<V> valueType, Class<? extends T> actionType) {
        simpleMenuAction(typeId, TypeToken.get(valueType), actionType);
    }

    public void menuAction(String typeId, Class<? extends MenuAction> type) {
        menuActionSerializer.registerType(typeId, type);
    }

    public Consumer<TypeSerializerCollection.Builder> registrator() {
        return builder -> {
            builder.registerExact(new TypeToken<List<Action>>() {}, new KeyedTypeListSerializer<>(genericActionSerializer));
            builder.registerExact(new TypeToken<List<MenuAction>>() {}, new KeyedTypeListSerializer<>(menuActionSerializer));
        };
    }
}
