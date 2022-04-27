package dev.projectg.crossplatforms.action;

import com.google.inject.Injector;
import dev.projectg.crossplatforms.interfacing.java.MenuAction;
import dev.projectg.crossplatforms.serialize.KeyedTypeListSerializer;
import dev.projectg.crossplatforms.serialize.KeyedTypeSerializer;
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

    public <V> void simpleGenericAction(String typeId, TypeToken<V> valueType, Class<? extends Action> actionType) {
        genericActionSerializer.registerSimpleType(typeId, valueType, actionType);
        menuActionSerializer.registerSimpleType(typeId, valueType, actionType);
    }

    public <V> void simpleGenericAction(String typeId, Class<V> valueType, Class<? extends Action> actionType) {
        simpleGenericAction(typeId, TypeToken.get(valueType), actionType);
    }

    public void genericAction(String typeId, Class<? extends Action> type) {
        genericActionSerializer.registerType(typeId, type);
        menuActionSerializer.registerType(typeId, type);
    }

    public <V> void simpleMenuAction(String typeId, TypeToken<V> valueType, Class<? extends MenuAction> actionType) {
        menuActionSerializer.registerSimpleType(typeId, valueType, actionType);
    }

    public <V> void simpleMenuAction(String typeId, Class<V> valueType, Class<? extends MenuAction> actionType) {
        simpleMenuAction(typeId, TypeToken.get(valueType), actionType);
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
