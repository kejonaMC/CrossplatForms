package dev.projectg.crossplatforms.config.keyedserializer;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class MultiMessage implements Message {

    public static final String IDENTIFIER = "messages";

    @Required
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
        return IDENTIFIER;
    }

    @Override
    public Object value() {
        return this;
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
}
