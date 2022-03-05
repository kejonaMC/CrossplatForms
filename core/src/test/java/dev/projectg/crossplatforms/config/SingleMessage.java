package dev.projectg.crossplatforms.config;

import org.jetbrains.annotations.NotNull;

public class SingleMessage extends SimpleType<String> implements Message {

    public static final String IDENTIFIER = "message";

    public SingleMessage(@NotNull String value) {
        super(value);
    }

    @Override
    public void send() {
        System.out.println(value());
    }

    @Override
    public String identifier() {
        return IDENTIFIER;
    }
}
