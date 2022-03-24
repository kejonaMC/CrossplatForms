package dev.projectg.crossplatforms.command.custom;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

public class InterceptCommandSerializer implements TypeSerializer<InterceptCommand> {

    @Override
    public InterceptCommand deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Pattern pattern = node.node(InterceptCommand.PATTERN_KEY).get(Pattern.class);
        String exact = node.node(InterceptCommand.EXACT_KEY).get(String.class);

        if (pattern != null) {
            if (exact != null) {
                throw new SerializationException("Both 'exact' and 'pattern' specified. Only one must be specified.");
            } else {
                return new InterceptCommand(pattern);
            }
        } else if (exact != null) {
            return new InterceptCommand(exact);
        } else {
            throw new SerializationException("Must specify either an exact string 'exact' or a regex expression 'pattern'");
        }
    }

    @Override
    public void serialize(Type type, @Nullable InterceptCommand obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        Pattern pattern = obj.getPattern();
        String exact = obj.getExact();

        if (pattern != null) {
            if (exact != null) {
                throw new SerializationException("Both pattern and exact are not null in: " + obj);
            } else {
                node.set(Pattern.class, pattern);
            }
        } else if (exact != null) {
            node.set(String.class, exact);
        } else {
            throw new SerializationException("Both pattern and exact are null in: " + obj);
        }
    }
}
