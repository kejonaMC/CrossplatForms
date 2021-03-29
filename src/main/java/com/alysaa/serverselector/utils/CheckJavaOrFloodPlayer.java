package com.alysaa.serverselector.utils;

import com.alysaa.serverselector.GServerSelector;
import org.geysermc.connector.GeyserConnector;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public class CheckJavaOrFloodPlayer {
    /**
     * Determines if a player is from Bedrock
     * @param uuid the UUID to determine
     * @return true if the player is from Bedrock
     */
    public static boolean isFloodgatePlayer(UUID uuid) {
        if (!GServerSelector.plugin.getConfig().getBoolean("EnableSelector")){
            return GeyserConnector.getInstance().getPlayerByUuid(uuid) != null;
        } else {
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        }
    }
}