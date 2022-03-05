package dev.projectg.crossplatforms.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class MessageList implements Message<List<String>> {

    @Required
    private String prefix = "";

    @Required
    private List<String> list = new ArrayList<>();

    @Override
    public void send() {
        get().forEach(System.out::println);
    }

    @Override
    public List<String> get() {
        return list.stream().map(message -> prefix + message).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageList that = (MessageList) o;
        return prefix.equals(that.prefix) && list.equals(that.list);
    }
}
