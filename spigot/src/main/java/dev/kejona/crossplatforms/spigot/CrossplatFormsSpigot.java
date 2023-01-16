package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.spigot.adapter.VersionIndexer;
import dev.kejona.crossplatforms.spigot.adapter.VersionIndexResult;

public class CrossplatFormsSpigot extends SpigotBase {

    @Override
    public VersionIndexResult findVersionAdapter() {
        String serverPackage = getServer().getClass().getPackage().getName();
        logger.debug("Server package: " + serverPackage);
        // +2 to remove the ".v"
        String version = serverPackage.substring(serverPackage.lastIndexOf('.') + 2);
        logger.debug("NMS version: " + version);

        return new VersionIndexer().findLenientAdapter(version);
    }
}
