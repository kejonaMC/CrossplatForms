package dev.kejona.crossplatforms.action;

import dev.kejona.crossplatforms.interfacing.java.MenuAction;
import dev.kejona.crossplatforms.serialize.TypeResolver;
import dev.kejona.crossplatforms.serialize.ValuedTypeSerializer;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class ActionSerializer {

    private final ValuedTypeSerializer<Action> genericActionSerializer = new ValuedTypeSerializer<>();
    private final ValuedTypeSerializer<MenuAction> menuActionSerializer = new ValuedTypeSerializer<>();

    public void genericAction(String typeId, Class<? extends Action> type) {
        genericActionSerializer.registerType(typeId, type);
        menuActionSerializer.registerType(typeId, type);
    }

    public void genericAction(String typeId, Class<? extends Action> type, TypeResolver resolver) {
        genericActionSerializer.registerType(typeId, type, resolver);
        menuActionSerializer.registerType(typeId, type, resolver);
    }

    public void menuAction(String typeId, Class<? extends MenuAction> type) {
        menuActionSerializer.registerType(typeId, type);
    }

    public void menuAction(String typeId, Class<? extends MenuAction> type, TypeResolver resolver) {
        menuActionSerializer.registerType(typeId, type, resolver);
    }

    public void register(TypeSerializerCollection.Builder builder) {
        builder.registerExact(new TypeToken<Action>() {}, genericActionSerializer);
        builder.registerExact(new TypeToken<MenuAction>() {}, menuActionSerializer);
    }
}
