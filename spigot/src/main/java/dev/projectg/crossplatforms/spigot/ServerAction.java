package dev.projectg.crossplatforms.spigot;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

@ToString
@ConfigSerializable
public class ServerAction implements Action {

    @Required
    String server = null;

    @Override
    public void affectPlayer(@NotNull FormPlayer player, @NotNull Map<String, String> additionalPlaceholders, @NotNull InterfaceManager interfaceManager, @NotNull BedrockHandler bedrockHandler) {
        PlaceholderHandler placeholders = CrossplatForms.getInstance().getPlaceholders();
        String resolved = placeholders.setPlaceholders(player, server, additionalPlaceholders);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(stream)) {
            Logger.getLogger().debug("Attempting to send " + player.getName() + " to BungeeCord server " + server);
            out.writeUTF("Connect");
            out.writeUTF(resolved);
            ((Player) player.getHandle()).sendPluginMessage(CrossplatFormsSpigot.getInstance(), "BungeeCord", stream.toByteArray());
        } catch (IOException e) {
            Logger.getLogger().severe("Failed to send a plugin message to BungeeCord!");
            e.printStackTrace();
        }
    }
}