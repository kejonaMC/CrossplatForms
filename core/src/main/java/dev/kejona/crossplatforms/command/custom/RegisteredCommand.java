package dev.kejona.crossplatforms.command.custom;

import dev.kejona.crossplatforms.command.CommandType;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;

import java.util.Locale;

@ToString(callSuper = true)
@ConfigSerializable
public class RegisteredCommand extends CustomCommand {

    public static final String TYPE = CommandType.REGISTER.name().toLowerCase(Locale.ROOT);

    private Literals command;

    private transient boolean enable = true;

    public Literals literals() {
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
    private void postProcess() {
        if (command == null) {
            command = Literals.of(new String[]{getIdentifier()});
        }
    }
}
