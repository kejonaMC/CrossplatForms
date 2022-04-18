package dev.projectg.crossplatforms.proxy;

import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import dev.simplix.cirrus.common.configuration.impl.SimpleMenuConfiguration;

public class CirrusInterfacer extends InterfaceManager {

    public CirrusInterfacer(ServerHandler serverHandler,
                            BedrockHandler bedrockHandler,
                            BedrockFormRegistry bedrockRegistry,
                            JavaMenuRegistry javaRegistry) {
        super(serverHandler, bedrockHandler, bedrockRegistry, javaRegistry);
    }

    @Override
    public void sendMenu(FormPlayer player, JavaMenu menu) {

    }
}
