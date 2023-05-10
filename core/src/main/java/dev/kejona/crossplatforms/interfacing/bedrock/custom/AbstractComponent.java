package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import dev.kejona.crossplatforms.context.PlayerContext;

import javax.annotation.Nonnull;

/**
 * Allows for Component implementations to return copies of their exact same type, using generics.
 * This class exists to avoid unnecessary generics usage in {@link CustomComponent} that would clutter things.
 */
public abstract class AbstractComponent<C extends CustomComponent> extends CustomComponent {

    protected AbstractComponent() {
        super();
    }

    public AbstractComponent(@Nonnull String text) {
        super(text);
    }

    @Override
    public abstract C copy();

    @Override
    public final C preparedCopy(PlayerContext context) {
        C copy = copy();
        copy.prepare(context);
        return copy;
    }
}
