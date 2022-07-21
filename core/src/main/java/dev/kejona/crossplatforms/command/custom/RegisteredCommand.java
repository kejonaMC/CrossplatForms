package dev.kejona.crossplatforms.command.custom;

import dev.kejona.crossplatforms.command.CommandType;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Locale;

@ToString(callSuper = true)
@ConfigSerializable
public class RegisteredCommand extends CustomCommand {

    public static final String TYPE = CommandType.REGISTER.name().toLowerCase(Locale.ROOT);

    private Arguments command;

    private transient boolean enable = true;

    public Arguments literals() {
        return command;
    }

    public void enable(boolean state) {
        this.enable = state;
    }

    public boolean isEnabled() {
        return enable;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @PostProcess
    protected void postProcess() {
        if (command == null) {
            command = Arguments.of(new String[]{getIdentifier()});
        }
    }
}
