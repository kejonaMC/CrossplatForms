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

    /**
     * Resolve placeholders in all applicable components of the ClickActions
     * @param resolver The placeholder resolver.
     * @return A new instance of the click action with any placeholders resolved
     */
    public ClickAction withPlaceholders(Function<String, String> resolver) {
        ClickAction action = new ClickAction();
        if (commands != null) {
            action.commands = new ArrayList<>();
            for (String command : this.commands) {
                action.commands.add(resolver.apply(command));
            }
        }

        action.server = resolver.apply(this.server);

        return action;
    }
}
