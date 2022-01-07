package dev.projectg.crossplatforms.form;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
@ConfigSerializable
public class ClickAction {

    @Nullable
    private List<String> commands;

    @Nullable
    private String server;

    public ClickAction withPlaceholders(Function<String, String> applyPlaceholders) {
        ClickAction action = new ClickAction();
        if (commands != null) {
            action.commands = new ArrayList<>();
            for (String command : this.commands) {
                action.commands.add(applyPlaceholders.apply(command));
            }
        }

        action.server = applyPlaceholders.apply(this.server);

        return action;
    }
}
