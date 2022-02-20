package dev.projectg.crossplatforms.interfacing;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.utils.PlaceholderUtils;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ToString
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class BasicClickAction implements ClickAction {

    // todo: usage of this should be turned into a map of click actions. key of the click action defines the click actions implementation. different platforms can defined extra click actions.

    @Nonnull
    protected List<String> commands = Collections.emptyList();

    @Nullable
    protected String server = null;

    @Nullable
    protected String form = null;

    @Override
    public void affectPlayer(@Nonnull Player player, @Nonnull Map<String, String> additionalPlaceholders, @Nonnull InterfaceManager interfaceManager, @Nonnull BedrockHandler bedrockHandler) {
        // Get the commands from the list of commands and replace any playerName placeholders
        for (String command : commands) {
            interfaceManager.runCommand(
                    PlaceholderUtils.setPlaceholders(player, command, additionalPlaceholders),
                    player.getUniqueId());
        }

        if (form != null && !form.isBlank()) {
            Interface ui = interfaceManager.getInterface(form, bedrockHandler.isBedrockPlayer(player.getUniqueId()));
            if (ui == null) {
                Logger logger = Logger.getLogger();
                logger.severe("Attempted to make a player open a form or menu '" + form + "', but it does not exist. This is a configuration error!");
                // todo: remove interface from the registry
                if (logger.isDebug()) {
                    Thread.dumpStack();
                }
            } else {
                ui.send(new SpigotPlayer(player));
            }
        }

        if (server != null && !server.isBlank()) {
            String resolved = PlaceholderUtils.setPlaceholders(player, server, additionalPlaceholders);

            // This should never be out of bounds considering its size is the number of valid buttons
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(stream)) {
                Logger.getLogger().debug("Attempting to send " + player.getName() + " to BungeeCord server " + server);
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
