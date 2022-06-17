package dev.kejona.crossplatforms.command.custom;

import dev.kejona.crossplatforms.serialize.ValuedTypeSerializer;

public class CustomCommandSerializer extends ValuedTypeSerializer<CustomCommand> {

    public CustomCommandSerializer() {
        super("method");
        registerType(RegisteredCommand.TYPE, RegisteredCommand.class);
        registerType(InterceptCommand.INTERCEPT_PASS_TYPE, InterceptCommand.class);
        registerType(InterceptCommand.INTERCEPT_CANCEL_TYPE, InterceptCommand.class);
    }
}
