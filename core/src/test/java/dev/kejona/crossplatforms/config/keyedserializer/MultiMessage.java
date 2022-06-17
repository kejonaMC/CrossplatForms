package dev.kejona.crossplatforms.config.keyedserializer;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ConfigSerializable
public class MultiMessage implements Message {

    public static final String TYPE = "messages";

    private String prefix = "";

    @Required
    private List<String> list = new ArrayList<>();

    @SuppressWarnings("unused") //configurate
    private MultiMessage() {

    }

    public MultiMessage(String prefix, List<String> messages) {
        this.prefix = prefix;
        this.list = messages;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void send() {
        list.forEach(System.out::println);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiMessage that = (MultiMessage) o;
        return prefix.equals(that.prefix) && list.equals(that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, list);
    }
}
