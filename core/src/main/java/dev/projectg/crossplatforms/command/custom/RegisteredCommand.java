package dev.projectg.crossplatforms.command.custom;

import dev.projectg.crossplatforms.command.CommandType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Locale;

@ConfigSerializable
public class RegisteredCommand extends CustomCommand {

    public static final String TYPE = CommandType.REGISTER.name().toLowerCase(Locale.ROOT);

    @Override
    public String type() {
        return TYPE;
    }
}
