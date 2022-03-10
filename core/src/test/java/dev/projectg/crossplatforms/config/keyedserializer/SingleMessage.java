package dev.projectg.crossplatforms.config.keyedserializer;

import dev.projectg.crossplatforms.config.serializer.SimpleType;
import org.jetbrains.annotations.NotNull;

public class SingleMessage extends SimpleType<String> implements Message {

    public static final String IDENTIFIER = "message";

    public SingleMessage( @NotNull String value) {
        super(IDENTIFIER, value);
    }

    @Override
    public void send() {
        System.out.println(value());
    }
}
