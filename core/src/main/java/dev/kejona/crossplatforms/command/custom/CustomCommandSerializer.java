package dev.kejona.crossplatforms.command.custom;

import dev.kejona.crossplatforms.serialize.KeyedTypeSerializer;

public class CustomCommandSerializer extends KeyedTypeSerializer<CustomCommand> {

    public CustomCommandSerializer() {
        super("method");
        registerType(RegisteredCommand.TYPE, RegisteredCommand.class);
        registerType(InterceptCommand.INTERCEPT_PASS_TYPE, InterceptCommand.class);
        registerType(InterceptCommand.INTERCEPT_CANCEL_TYPE, InterceptCommand.class);
    }
}
