package dev.projectg.geyserhub.module.menu.java;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickHandler {

    private static final Map<Player, ClickHandler> TRACKED_HANDLERS = new HashMap<>();

    private final Map<Integer, List<String>> commands;
    private final Map<Integer, String> servers;


    public ClickHandler(Player player, Map<Integer, List<String>> commands, Map<Integer, String> servers) {
        this.commands = commands;
        this.servers = servers;

        TRACKED_HANDLERS.put(player, this);
    }


    public static void process(Player player, int buttonId) {

        // todo

        TRACKED_HANDLERS.remove(player);
    }
}
