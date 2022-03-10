package dev.projectg.crossplatforms.form.component;

import dev.projectg.crossplatforms.Resolver;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.Input;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class ResolvePlaceholdersTest {

    private static final Resolver resolver = s -> s.replace("%1%", "one").replace("%two%", "2");
    
    @Test
    public void copyInputTest() {
        Input before = new Input("","type here", "words", true, Collections.emptyMap());
        Input after = before.copy();
        Assertions.assertEquals(before, after);
    }

    @Test
    public void resolveInputTest() {
        Input actual = new Input("words","%1%", "%two%", true, Collections.emptyMap()).withPlaceholders(resolver);
        Input expected = new Input("words","one", "2", true, Collections.emptyMap());
        Assertions.assertEquals(actual, expected);
    }
}
