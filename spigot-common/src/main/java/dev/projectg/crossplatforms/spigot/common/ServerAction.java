package dev.projectg.crossplatforms.spigot.common;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.SimpleAction;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

@ConfigSerializable
public class ServerAction extends SimpleAction<String> {

    public static JavaPlugin SENDER = null;

    public static final String IDENTIFIER = "server";

    public ServerAction(String value) {
        super(IDENTIFIER, value);
    }

    @Override
    public String type() {
        return IDENTIFIER;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders, @Nonnull InterfaceManager interfaceManager, @Nonnull BedrockHandler bedrockHandler) {
        PlaceholderHandler placeholders = CrossplatForms.getInstance().getPlaceholders();
        String resolved = placeholders.setPlaceholders(player, value(), additionalPlaceholders);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(stream)) {
            Logger.getLogger().debug("Attempting to send " + player.getName() + " to BungeeCord server " + value());
            out.writeUTF("Connect");
            out.writeUTF(resolved);
            ((Player) player.getHandle()).sendPluginMessage(SENDER, "BungeeCord", stream.toByteArray());
        } catch (IOException e) {
            Logger.getLogger().severe("Failed to send a plugin message to BungeeCord!");
            e.printStackTrace();
        }
    }
}
