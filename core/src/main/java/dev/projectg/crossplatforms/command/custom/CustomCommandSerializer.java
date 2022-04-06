package dev.projectg.crossplatforms.command.custom;

import dev.projectg.crossplatforms.serialize.ValuedTypeSerializer;

public class CustomCommandSerializer extends ValuedTypeSerializer<CustomCommand> {

    public CustomCommandSerializer() {
        super("method");
        registerType(RegisteredCommand.TYPE, RegisteredCommand.class);
        registerType(InterceptCommand.INTERCEPT_PASS_TYPE, InterceptCommand.class);
        registerType(InterceptCommand.INTERCEPT_CANCEL_TYPE, InterceptCommand.class);
    }
}
