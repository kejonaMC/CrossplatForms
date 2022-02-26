package dev.projectg.crossplatforms.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DispatchableCommandTest {

    @Test
    public void deserializeTest() {
        DispatchableCommand player = new DispatchableCommand(true, "give person dirt 64", false);
        Assertions.assertEquals(player, DispatchableCommandSerializer.deserialize("player; give person dirt 64"));
        Assertions.assertEquals(player, DispatchableCommandSerializer.deserialize("player;give person dirt 64"));

        DispatchableCommand op = new DispatchableCommand(true, "stop", true);
        Assertions.assertEquals(op, DispatchableCommandSerializer.deserialize("op; stop"));
        Assertions.assertEquals(op, DispatchableCommandSerializer.deserialize("op;stop"));

        DispatchableCommand console = new DispatchableCommand(false, "broadcast message", true);
        Assertions.assertEquals(console, DispatchableCommandSerializer.deserialize("console; broadcast message"));
        Assertions.assertEquals(console, DispatchableCommandSerializer.deserialize("console;broadcast message"));
        Assertions.assertEquals(console, DispatchableCommandSerializer.deserialize("broadcast message"));
    }
}
