package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.AnnotatedType;

public class OptionSerializer implements TypeSerializer.Annotated<Option> {

    @Override
    public Option deserialize(AnnotatedType type, ConfigurationNode node) throws SerializationException {
        String display = node.getString();
        if (display == null) {
            throw new SerializationException("Must be a string");
        }
        return new Option(display);
    }

    @Override
    public void serialize(AnnotatedType type, @Nullable Option option, ConfigurationNode node) throws SerializationException {
        if (option == null) {
            node.raw(null);
        } else {
            node.set(option.display());
        }
    }
}
