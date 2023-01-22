package dev.kejona.crossplatforms.form.component;

import dev.kejona.crossplatforms.FakePlayer;
import dev.kejona.crossplatforms.context.PlayerContext;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.Input;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResolvePlaceholdersTest {

    private static final FormPlayer PLAYER = new FakePlayer();
    private static final Resolver RESOLVER = Resolver.of(s -> s.replace("%1%", "one").replace("%two%", "2"));
    private static final PlayerContext CONTEXT = new PlayerContext(PLAYER, RESOLVER);
    
    @Test
    public void copyInputTest() {
        Input before = new Input("","type here", "words");
        Input after = before.copy();
        Assertions.assertEquals(before, after);
    }

    @Test
    public void resolveInputTest() {
        Input expected = new Input("words","one", "2");

        Input withActual = (Input) new Input("words","%1%", "%two%").preparedCopy(CONTEXT);
        Assertions.assertEquals(withActual, expected);

        Input setActual = new Input("words","%1%", "%two%");
        setActual.prepare(CONTEXT);
        Assertions.assertEquals(setActual, expected);
    }
}
