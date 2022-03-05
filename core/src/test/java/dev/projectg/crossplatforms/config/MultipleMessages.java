package dev.projectg.crossplatforms.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class MultipleMessages implements Message {

    public static final String IDENTIFIER = "messages";

    @Required
    private String prefix = "";

    @Required
    private List<String> list = new ArrayList<>();

    @Override
    public void send() {
        list.forEach(System.out::println);
    }

    @Override
    public String identifier() {
        return IDENTIFIER;
    }

    @Override
    public MultipleMessages value() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultipleMessages that = (MultipleMessages) o;
        return prefix.equals(that.prefix) && list.equals(that.list);
    }
}
