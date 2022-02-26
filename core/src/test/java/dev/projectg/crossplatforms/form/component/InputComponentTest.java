package dev.projectg.crossplatforms.form.component;

import com.google.gson.JsonPrimitive;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.Input;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InputComponentTest {

    @Test
    public void testFilterPlaceholders() {
        Input input = new Input("", "", "", true);
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT + " and " + Input.PLACEHOLDER_REPLACEMENT, input.parse(primitive("%player_name% and %player_uuid%")));
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT + " and " + Input.PLACEHOLDER_REPLACEMENT, input.parse(primitive("{player_name} and {player_uuid}")));
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT, input.parse(primitive("%%player_balance%%")));
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT, input.parse(primitive("{{player_balance}}")));
        Assertions.assertEquals(Input.PLACEHOLDER_REPLACEMENT, input.parse(primitive("%%nested_placeholder%_other%")));
        Assertions.assertEquals("I took 50%.", input.parse(primitive("I took 50%.")));
        Assertions.assertEquals("I took 50% and then 20%.", input.parse(primitive("I took 50% and then 20%.")));
        Assertions.assertEquals("%%", input.parse(primitive("%%")));
    }

    private JsonPrimitive primitive(String string) {
        return new JsonPrimitive(string);
    }
}
