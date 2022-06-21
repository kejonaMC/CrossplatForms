package dev.kejona.crossplatforms.form.component;

import com.google.common.collect.ImmutableMap;
import dev.kejona.crossplatforms.FakePlayer;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.Input;
import dev.kejona.crossplatforms.parser.BlockPlaceholderParser;
import dev.kejona.crossplatforms.parser.ReplacementParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class InputComponentTest {

    private static final FormPlayer player = new FakePlayer();

    @Test
    public void testFilterPlaceholders() {
        Input input = blankInput();
        input.parsers(Collections.singletonList(new BlockPlaceholderParser()));
        Assertions.assertEquals(BlockPlaceholderParser.PLACEHOLDER_REPLACEMENT + " and " + BlockPlaceholderParser.PLACEHOLDER_REPLACEMENT, input.parse(player, "%player_name% and %player_uuid%"));
        Assertions.assertEquals(BlockPlaceholderParser.PLACEHOLDER_REPLACEMENT + " and " + BlockPlaceholderParser.PLACEHOLDER_REPLACEMENT, input.parse(player, "{player_name} and {player_uuid}"));
        Assertions.assertEquals(BlockPlaceholderParser.PLACEHOLDER_REPLACEMENT, input.parse(player, "%%player_balance%%"));
        Assertions.assertEquals(BlockPlaceholderParser.PLACEHOLDER_REPLACEMENT, input.parse(player, "{{player_balance}}"));
        Assertions.assertEquals(BlockPlaceholderParser.PLACEHOLDER_REPLACEMENT, input.parse(player, "%%nested_placeholder%_other%"));
        Assertions.assertEquals("I took 50%.", input.parse(player, "I took 50%."));
        Assertions.assertEquals("I took 50% and then 20%.", input.parse(player, "I took 50% and then 20%."));
        Assertions.assertEquals("%%", input.parse(player, "%%"));
    }

    @Test
    public void testReplacements() {
        Input withDashes = blankInput();
        withDashes.parser(new ReplacementParser(ImmutableMap.of(" ", "-")));
        Assertions.assertEquals("marshy-waters", withDashes.parse(player, "marshy waters"));

        Input useless = blankInput();
        useless.parser(new ReplacementParser(ImmutableMap.of(" ", "-", "-", " ")));
        Assertions.assertEquals("Big Green Hill", useless.parse(player, "Big Green Hill"));

        Input cascading = blankInput();
        cascading.parser(new ReplacementParser(ImmutableMap.of("a", "b", "bb", "aa")));
        Assertions.assertEquals("aa aa", cascading.parse(player, "ab ba"));
    }

    private static Input blankInput() {
        return Input.builder().build();
    }
}
