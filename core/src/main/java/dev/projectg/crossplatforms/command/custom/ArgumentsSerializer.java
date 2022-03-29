package dev.projectg.crossplatforms.command.custom;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ArgumentsSerializer implements TypeSerializer<Arguments> {

    @Override
    public Arguments deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String joined = node.getString();
        if (joined == null) {
            throw new SerializationException("Command arguments cannot be null");
        }
        String[] separated = joined.split(" ");
        if (separated.length < 1) {
            throw new SerializationException("Command arguments length was " + separated.length + ", needs to be greater than 0.");
        }
        return Arguments.of(separated);
    }

    @Override
    public void serialize(Type type, @Nullable Arguments obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }
        String joined = String.join(" ", obj.source());
        node.set(String.class, joined);
    }
}
