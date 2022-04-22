package dev.projectg.crossplatforms.interfacing;

import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;

public class NoMenusInterfacer extends InterfaceManager {

    public NoMenusInterfacer(ServerHandler serverHandler, BedrockHandler bedrockHandler, BedrockFormRegistry bedrockRegistry, JavaMenuRegistry javaRegistry) {
        super(serverHandler, bedrockHandler, bedrockRegistry, javaRegistry);
    }

    @Override
    public void sendMenu(FormPlayer player, JavaMenu menu) {
        throw new UnsupportedOperationException("Inventory menus are not supported.");
    }

    @Override
    public boolean supportsMenus() {
        return false;
    }
}
