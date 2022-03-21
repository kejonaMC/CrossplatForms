package dev.projectg.crossplatforms.command.proxy;

import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.handler.ServerHandler;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Small map-wrapper class to facilitate {@link ServerHandler} implementations if desired
 */
public abstract class ProxyCommandCache {

    protected static final Pattern COMMAND_PATTERN = Pattern.compile("\\s");
    protected static final String PERMISSION_MESSAGE = Constants.MESSAGE_PREFIX + "You don't have permission to run that.";

    protected final HashMap<String, CustomCommand> proxyCommands = new HashMap<>();

    public void registerProxyCommand(CustomCommand proxyCommand) {
        proxyCommands.put(proxyCommand.getIdentifier(), proxyCommand);
    }

    public void clearProxyCommands() {
        proxyCommands.clear();
    }
}
