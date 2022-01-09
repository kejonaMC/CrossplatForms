package dev.projectg.crossplatforms.handler.server;

import java.util.UUID;

public interface Player {


    UUID getUUID();
    String getName();
    boolean hasPermission(String permission);
    Object getHandle();
}
