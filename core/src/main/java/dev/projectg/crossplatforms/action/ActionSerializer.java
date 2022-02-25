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
import java.util.function.Function;

/**
 * Deserializes a map of action type -> action, into a list of actions. Deserializes each action based off its type,
 * which must be registered.
 */
public class ActionSerializer implements TypeSerializer<List<Action>> {

    private final Map<String, Class<? extends Action>> actions = new HashMap<>();
    private final Map<String, SimpleAction.Registration<?>> simpleActions = new HashMap<>();

    public void register(String typeId, Class<? extends Action> type) {
        actions.put(typeId, type);
    }

    /**
     * Register a new {@link SimpleAction} implementation.
     * @param key The unique configuration key for the action
     * @param creator To create new instances of the implementation.
     * @param <T> The type of singleton value the {@link SimpleAction} consists of.
     */
    public <T> void registerSimple(String key, Function<T, SimpleAction<T>> creator) {
        registerSimple(key, new TypeToken<>() {}, creator);
    }

    private <T> void registerSimple(String key, TypeToken<T> type, Function<T, SimpleAction<T>> creator) {
        // this hackery is used to get around a java compiler bug (apparently a bug) regarding type parameter inference.
        // copy the following line into the public registerSimple method to experience it.
        simpleActions.put(key, new SimpleAction.Registration<>(type, creator));
    }

    @Override
    public List<Action> deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Map<String, ConfigurationNode> childMap = node.get(new TypeToken<>() {});
        if (childMap == null) {
            throw new SerializationException("Actions at " + node.path() + " is empty!");
        }

        List<Action> actions = new ArrayList<>();
        for (Map.Entry<String, ConfigurationNode> entry : childMap.entrySet()) {
            String key = entry.getKey();
            ConfigurationNode actionNode = entry.getValue();
            Action action;

            Class<? extends Action> actionType = this.actions.get(key);
            if (actionType == null) {
                SimpleAction.Registration<?> registration = simpleActions.get(key);
                if (registration == null) {
                    throw new SerializationException("Unsupported action type: " + key);
                } else {
                    action = registration.fromNode(actionNode);
                }
            } else {
                action = actionNode.get(actionType);
            }
            actions.add(action);
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
