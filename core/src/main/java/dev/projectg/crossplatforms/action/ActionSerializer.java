package dev.projectg.crossplatforms.action;

import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionSerializer implements TypeSerializer<List<Action>> {

    Map<String, Class<? extends Action>> typeRegistry = new HashMap<>();

    public void register(String typeId, Class<? extends Action> type) {
        typeRegistry.put(typeId, type);
    }

    @Override
    public List<Action> deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Map<String, ConfigurationNode> childMap = node.get(new TypeToken<>() {});
        if (childMap == null) {
            throw new SerializationException("Actions at " + node.path() + " is empty!");
        }

        List<Action> actions = new ArrayList<>();
        for (Map.Entry<String, ConfigurationNode> entry : childMap.entrySet()) {
            Class<? extends Action> actionType = typeRegistry.get(entry.getKey());
            if (actionType == null) {
                throw new SerializationException("Unsupported action type: " + entry.getKey());
            }
            actions.add(entry.getValue().get(actionType));
        }

        return actions;
    }

    @Override
    public void serialize(Type type, @Nullable List<Action> actions, ConfigurationNode node) throws SerializationException {
        if (actions == null) {
            node.raw(null);
            return;
        }

        node.set(new TypeToken<>() {}, actions);
    }
}
