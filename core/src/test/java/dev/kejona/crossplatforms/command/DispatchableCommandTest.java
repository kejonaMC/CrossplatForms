package dev.kejona.crossplatforms.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static dev.kejona.crossplatforms.command.DispatchableCommandSerializer.deserialize;

public class DispatchableCommandTest {

    @Test
    public void deserializeTest() {
        DispatchableCommand player = new DispatchableCommand(true, "give person dirt 64", false);
        Assertions.assertEquals(player, deserialize("player; give person dirt 64"));
        Assertions.assertEquals(player, deserialize("player;give person dirt 64"));

        DispatchableCommand op = new DispatchableCommand(true, "stop", true);
        Assertions.assertEquals(op, deserialize("op; stop"));
        Assertions.assertEquals(op, deserialize("op;stop"));

        DispatchableCommand console = new DispatchableCommand("broadcast message");
        Assertions.assertEquals(console, deserialize("console; broadcast message"));
        Assertions.assertEquals(console, deserialize("console;broadcast message"));
        Assertions.assertEquals(console, deserialize("broadcast message"));
    }
}
