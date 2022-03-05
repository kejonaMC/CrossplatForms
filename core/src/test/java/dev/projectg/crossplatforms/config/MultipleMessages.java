package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.action.SimpleType;
import org.jetbrains.annotations.NotNull;

public class MultipleMessages extends SimpleType<String> implements Message<String> {

    public MultipleMessages(@NotNull String value) {
        super(value);
    }

    @Override
    public void send() {
        System.out.println(getValue());
    }

    @Override
    public String get() {
        return getValue();
    }
}
