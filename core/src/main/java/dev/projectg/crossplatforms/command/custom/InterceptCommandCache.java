package dev.projectg.crossplatforms.command.custom;

import dev.projectg.crossplatforms.handler.ServerHandler;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Small map-wrapper class to facilitate {@link ServerHandler} implementations if desired
 */
public abstract class InterceptCommandCache {

    private final Map<String, InterceptCommand> exactCommands = new HashMap<>();
    private final List<InterceptCommand> patternCommands = new ArrayList<>();

    public void registerInterceptCommand(InterceptCommand proxyCommand) {
        String exact = proxyCommand.getExact();
        if (exact != null) {
            exactCommands.put(proxyCommand.getExact(), proxyCommand);
        } else {
            Objects.requireNonNull(proxyCommand.getPattern());
            patternCommands.add(proxyCommand);
        }
    }

    public void clearInterceptCommands() {
        exactCommands.clear();
        patternCommands.clear();
    }

    @Nullable
    public InterceptCommand findCommand(String input) {
        // attempt to find an exact match
        InterceptCommand command = exactCommands.get(input);
        if (command == null) {
            // no command found for this exact string, try finding something that matches
            for (InterceptCommand patternCommand : patternCommands) {
                if (Objects.requireNonNull(patternCommand.getPattern()).matcher(input).matches()) {
                    command = patternCommand;
                    break;
                }
            }
        }
        return command;
    }
}
