package dev.projectg.crossplatforms.action;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ToString
@ConfigSerializable
public class CommandsAction extends ArrayList<DispatchableCommand> implements Action {

    public CommandsAction(List<DispatchableCommand> commands) {
        super(commands);
    }

    @Override
    public void affectPlayer(@NotNull FormPlayer player, @NotNull Map<String, String> additionalPlaceholders, @NotNull InterfaceManager interfaceManager, @NotNull BedrockHandler bedrockHandler) {
        PlaceholderHandler placeholders = CrossplatForms.getInstance().getPlaceholders();
        List<DispatchableCommand> resolved = super.stream()
                .map(command -> command.withCommand(placeholders.setPlaceholders(player, command.command(), additionalPlaceholders)))
                .collect(Collectors.toList());

        CrossplatForms.getInstance().getServerHandler().dispatchCommands(player.getUuid(), resolved);
    }
}

