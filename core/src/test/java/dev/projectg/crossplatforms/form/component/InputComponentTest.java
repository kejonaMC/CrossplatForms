package dev.projectg.crossplatforms.form.component;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonPrimitive;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.Input;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InputComponentTest {

    @Test
    public void testFilterPlaceholders() {
        Input input = Input.builder().blockPlaceholders(true).build();
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT + " and " + Input.PLACEHOLDER_REPLACEMENT, input.parse(primitive("%player_name% and %player_uuid%")));
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT + " and " + Input.PLACEHOLDER_REPLACEMENT, input.parse(primitive("{player_name} and {player_uuid}")));
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT, input.parse(primitive("%%player_balance%%")));
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT, input.parse(primitive("{{player_balance}}")));
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT, input.parse(primitive("%%nested_placeholder%_other%")));
        Assertions.assertEquals("I took 50%.", input.parse(primitive("I took 50%.")));
        Assertions.assertEquals("I took 50% and then 20%.", input.parse(primitive("I took 50% and then 20%.")));
        Assertions.assertEquals("%%", input.parse(primitive("%%")));
    }

    @Test
    public void testReplacements() {
        Input withDashes = Input.builder().replacements(ImmutableMap.of(" ", "-")).build();
        Assertions.assertEquals("marshy-waters", withDashes.parse(primitive("marshy waters")));

        Input useless = Input.builder().replacements(ImmutableMap.of(" ", "-", "-", " ")).build();
        Assertions.assertEquals("Big Green Hill", useless.parse(primitive("Big Green Hill")));

        Input cascading = Input.builder().replacements(ImmutableMap.of("a", "b", "bb", "aa")).build();
        Assertions.assertEquals("aa aa", cascading.parse(primitive("ab ba")));
    }

    @Test
    public void testHideCensoredPlaceholder() {
        Input input = Input.builder().blockPlaceholders(true).replacements(ImmutableMap.of(Input.PLACEHOLDER_REPLACEMENT, "")).build();
        Assertions.assertEquals("My location is ", input.parse(primitive("My location is {player_location}")));
    }

    private static JsonPrimitive primitive(String string) {
        return new JsonPrimitive(string);
    }
}
