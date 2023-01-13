package dev.kejona.crossplatforms.handler;

import org.geysermc.cumulus.form.Form;

import java.util.UUID;

public interface BedrockHandler {

    String getType();

    boolean isBedrockPlayer(UUID uuid);

    void sendForm(UUID uuid, Form form);

    /**
     * Determines if this BedrockHandler executes Form response handlers in a way that allows for thread safe access to the
     * server API of the given implementation. For example, Floodgate executes response dev.kejona.crossplatforms.spigot.handler on Bukkit's main thread,
     * but Geyser doesn't. On proxy implementations like BungeeCord or Velocity where there is no "main thread", this method
     * is not as meaningful.
     *
     * @return true if the given BedrockHandler executes response handlers in a fashion that allows for thread safe access
     * to the current server API
     */
    boolean executesResponseHandlersSafely();

    /**
     * Transfer a bedrock player to a given server. You should check if the given {@link FormPlayer} is actually a Bedrock
     * player before calling this.
     * @param player The bedrock player to transfer
     * @param address The address of the new server to transfer to
     * @param port the port of the new server to transfer to
     * @return true if it was a success
     */
    boolean transfer(FormPlayer player, String address, int port);

    static BedrockHandler empty() {
        return Empty.INSTANCE;
    }

    /**
     * Used when Geyser nor Floodgate are installed. If this is used, modules that only handle Bedrock players should be disabled.
     */
    class Empty implements BedrockHandler {

        public static BedrockHandler INSTANCE = new Empty();

        @Override
        public String getType() {
            return "None";
        }

        @Override
        public boolean isBedrockPlayer(UUID uuid) {
            return false;
        }

        @Override
        public void sendForm(UUID uuid, Form form) {
            throw new UnsupportedOperationException("Cannot send forms");
        }

        @Override
        public boolean executesResponseHandlersSafely() {
            return false;
        }

        @Override
        public boolean transfer(FormPlayer player, String address, int port) {
            throw new UnsupportedOperationException("Cannot transfer players");
        }
    }
}
