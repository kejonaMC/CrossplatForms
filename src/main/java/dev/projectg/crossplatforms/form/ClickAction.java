package dev.projectg.crossplatforms.form;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.utils.InterfaceUtils;
import dev.projectg.crossplatforms.utils.PlaceholderUtils;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ToString
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
        if (this.commands != null) {
            action.commands = new ArrayList<>();
            for (String command : this.commands) {
                action.commands.add(resolver.apply(command));
            }
        }

        if (this.server != null) {
            action.server = resolver.apply(this.server);
        }

        return action;
    }

    public void affectPlayer(@Nonnull Player player) {
        affectPlayer(player, Collections.emptyMap());
    }

    public void affectPlayer(@Nonnull Player player, @Nonnull Map<String, String> additionalPlaceholders) {
        if (commands != null && !commands.isEmpty()) {
            // Get the commands from the list of commands and replace any playerName placeholders
            for (String command : commands) {
                InterfaceUtils.runCommand(PlaceholderUtils.setPlaceholders(player, command, additionalPlaceholders), player);
            }
        }

        if (server != null && !server.isEmpty()) {
            String resolved = PlaceholderUtils.setPlaceholders(player, server, additionalPlaceholders);

            // This should never be out of bounds considering its size is the number of valid buttons
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(stream)) {
                out.writeUTF("Connect");
                out.writeUTF(resolved);
                player.sendPluginMessage(CrossplatForms.getInstance(), "BungeeCord", stream.toByteArray());
            } catch (IOException e) {
                Logger.getLogger().severe("Failed to send a plugin message to BungeeCord!");
                e.printStackTrace();
            }
        }
    }
}
