package dev.kejona.crossplatforms.interfacing.bedrock;

import dev.kejona.crossplatforms.utils.ParseUtils;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@ConfigSerializable
public abstract class OptionalElement {

    protected List<String> shouldShow = Collections.emptyList();

    /**
     * This should only be called after placeholders have been resolved, and ideally only called once.
     */
    protected boolean show() {
        return allTrue(shouldShow.stream());
    }

    public static boolean allTrue(Stream<String> booleans) {
        return booleans.allMatch(s -> ParseUtils.getBoolean(s, false));
    }
}
