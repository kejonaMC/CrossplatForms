package dev.projectg.crossplatforms.action;

import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyedSerializer<T> {

    @Getter
    private final SingleSerializer singleSerializer = new SingleSerializer();

    @Getter
    private final MapSerializer mapSerializer = new MapSerializer();

    public Map<String, T> types = new HashMap<>();

    public class SingleSerializer implements TypeSerializer<T> {

        @Override
        public T deserialize(Type type, ConfigurationNode node) throws SerializationException {
            return null;
        }

        @Override
        public void serialize(Type type, @Nullable T obj, ConfigurationNode node) throws SerializationException {

        }
    }

    public class MapSerializer implements TypeSerializer<List<T>> {

        @Override
        public List<T> deserialize(Type type, ConfigurationNode node) throws SerializationException {
            return null;
        }

        @Override
        public void serialize(Type type, @Nullable List<T> obj, ConfigurationNode node) throws SerializationException {

        }
    }
 }
