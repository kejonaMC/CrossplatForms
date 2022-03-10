package dev.projectg.crossplatforms.form.component;

import com.google.common.collect.ImmutableMap;
import dev.projectg.crossplatforms.FakePlayer;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.Input;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InputComponentTest {

    private static final FormPlayer player = new FakePlayer();

    @Test
    public void testFilterPlaceholders() {
        Input input = Input.builder().blockPlaceholders(true).build();
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT + " and " + Input.PLACEHOLDER_REPLACEMENT, input.parse(player, primitive("%player_name% and %player_uuid%")));
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT + " and " + Input.PLACEHOLDER_REPLACEMENT, input.parse(player, primitive("{player_name} and {player_uuid}")));
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT, input.parse(player, primitive("%%player_balance%%")));
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT, input.parse(player, primitive("{{player_balance}}")));
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT, input.parse(player, primitive("%%nested_placeholder%_other%")));
        Assertions.assertEquals("I took 50%.", input.parse(player, primitive("I took 50%.")));
        Assertions.assertEquals("I took 50% and then 20%.", input.parse(player, primitive("I took 50% and then 20%.")));
        Assertions.assertEquals("%%", input.parse(player, primitive("%%")));
    }

    @Test
    public void testReplacements() {
        Input withDashes = Input.builder().replacements(ImmutableMap.of(" ", "-")).build();
        Assertions.assertEquals("marshy-waters", withDashes.parse(player, primitive("marshy waters")));

        Input useless = Input.builder().replacements(ImmutableMap.of(" ", "-", "-", " ")).build();
        Assertions.assertEquals("Big Green Hill", useless.parse(player, primitive("Big Green Hill")));

        Input cascading = Input.builder().replacements(ImmutableMap.of("a", "b", "bb", "aa")).build();
        Assertions.assertEquals("aa aa", cascading.parse(player, primitive("ab ba")));
    }

    private static String primitive(String string) {
        return string; // too lazy to undo this
    }
}
