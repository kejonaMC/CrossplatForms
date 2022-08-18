package dev.kejona.crossplatforms.interfacing;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

@Accessors(fluent = true)
@Getter
public class Argument {

    private final String identifier;
    private final String placeholder;

    public Argument(String identifier) throws IllegalArgumentException {
        if (identifier.contains(" ")) {
            throw new IllegalArgumentException("Argument identifier must not contain spaces");
        }
        this.identifier = identifier;
        this.placeholder = "%" + identifier + "%";
    }


    /**
     * @param argument The argument to parse
     * @return the provided String if it passed validation
     * @throws ArgumentException if the provided argument did not pass validation
     */
    @Nonnull
    public String validate(@Nullable String argument) throws ArgumentException {
        if (argument == null) {
            throw ArgumentException.missingArg(identifier);
        }
        return argument;
    }

    public static class Serializer implements TypeSerializer<Argument> {

        @Override
        public Argument deserialize(Type type, ConfigurationNode node) throws SerializationException {

            String id = node.getString();
            if (id == null) {
                id = node.node("id").getString();
                if (id == null) {
                    throw new SerializationException("Does not declare argument 'id'");
                }
            }

            try {
                return new Argument(id);
            } catch (IllegalArgumentException e) {
                throw new SerializationException(e.getMessage());
            }
        }

        @Override
        public void serialize(Type type, @Nullable Argument arg, ConfigurationNode node) throws SerializationException {
            if (arg == null) {
                node.raw(null);
                return;
            }

            node.raw(arg.identifier());
        }
    }
}
