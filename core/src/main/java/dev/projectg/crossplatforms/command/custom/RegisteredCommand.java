package dev.projectg.crossplatforms.command.custom;

import dev.projectg.crossplatforms.command.CommandType;

import java.util.Locale;

public class RegisteredCommand extends CustomCommand {

    public static final String TYPE = CommandType.REGISTER.name().toLowerCase(Locale.ROOT);

    @Override
    public String type() {
        return TYPE;
    }
}
