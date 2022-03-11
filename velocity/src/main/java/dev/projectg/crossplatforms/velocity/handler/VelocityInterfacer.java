package dev.projectg.crossplatforms.velocity.handler;

import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;

public class VelocityInterfacer extends InterfaceManager {

    public VelocityInterfacer(ServerHandler serverHandler, BedrockHandler bedrockHandler, BedrockFormRegistry bedrockRegistry, JavaMenuRegistry javaRegistry) {
        super(serverHandler, bedrockHandler, bedrockRegistry, javaRegistry);
    }

    @Override
    public void sendMenu(FormPlayer player, JavaMenu menu) {
        throw new UnsupportedOperationException("Velocity doesn't support Java Edition menus yet.");
    }
}
