package dev.projectg.crossplatforms.spigot.common;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.SimpleAction;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ServerAction extends SimpleAction<String> {

    public static final String TYPE = "server";

    private final transient JavaPlugin sender;
    private final transient Placeholders placeholders;

    @Inject
    public ServerAction(String value, Placeholders placeholders) {
        super(TYPE, value);
        this.sender = SpigotBase.getInstance();
        this.placeholders = placeholders;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders) {
        String resolved = placeholders.setPlaceholders(player, value(), additionalPlaceholders);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(stream)) {
            Logger.get().debug("Attempting to send " + player.getName() + " to BungeeCord server " + value());
            out.writeUTF("Connect");
            out.writeUTF(resolved);
            ((Player) player.getHandle()).sendPluginMessage(sender, "BungeeCord", stream.toByteArray());
        } catch (IOException e) {
            Logger.get().severe("Failed to send a plugin message to BungeeCord!");
            e.printStackTrace();
        }
    }
}
