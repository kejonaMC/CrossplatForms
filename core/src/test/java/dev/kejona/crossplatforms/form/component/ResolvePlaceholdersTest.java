package dev.kejona.crossplatforms.form.component;

import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.Input;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResolvePlaceholdersTest {

    private static final Resolver resolver = Resolver.of(s -> s.replace("%1%", "one").replace("%two%", "2"));
    
    @Test
    public void copyInputTest() {
        Input before = new Input("","type here", "words");
        Input after = before.copy();
        Assertions.assertEquals(before, after);
    }

    @Test
    public void resolveInputTest() {
        Input expected = new Input("words","one", "2");

        Input withActual = new Input("words","%1%", "%two%").preparedCopy(resolver);
        Assertions.assertEquals(withActual, expected);

        Input setActual = new Input("words","%1%", "%two%");
        setActual.prepare(resolver);
        Assertions.assertEquals(setActual, expected);
    }
}
