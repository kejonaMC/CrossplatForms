package dev.kejona.crossplatforms.command;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class DispatchableCommandSerializer implements TypeSerializer<DispatchableCommand> {

    private static final String PLAYER_PREFIX = "player;";
    private static final String OP_PREFIX = "op;";
    private static final String CONSOLE_PREFIX = "console;";

    public static DispatchableCommand deserialize(String value) {
        boolean player = value.startsWith(PLAYER_PREFIX);
        boolean op = value.startsWith(OP_PREFIX);
        boolean console = value.startsWith(CONSOLE_PREFIX);
        if (player || op || console) {
            // Split the input into two strings between ";" and get the second string
            value = value.split(";", 2)[1].trim();
            if (player) {
                return new DispatchableCommand(true, value, false);
            } else if (op) {
                return new DispatchableCommand(true, value, true);
            } else {
                return new DispatchableCommand(false, value, true);
            }
        } else {
            return new DispatchableCommand(false, value, true);
        }
    }

    @Override
    public DispatchableCommand deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String raw = node.getString();
        if (raw == null || raw.isEmpty()) {
            throw new SerializationException("Command at " + node.path() + " is empty!");
        }
        return deserialize(raw);
    }

    @Override
    public void serialize(Type type, @Nullable DispatchableCommand command, ConfigurationNode node) throws SerializationException {
        if (command == null) {
            node.raw(null);
            return;
        }

        String prefix;
        if (command.isPlayer()) {
            if (command.isOp()) {
                prefix = OP_PREFIX;
            } else {
                prefix = PLAYER_PREFIX;
            }
        } else {
            prefix = CONSOLE_PREFIX;
        }

        node.set(prefix + " " + command.getCommand());
    }
}
