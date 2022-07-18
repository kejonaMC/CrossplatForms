package dev.kejona.crossplatforms.interfacing.bedrock;

import dev.kejona.crossplatforms.utils.ParseUtils;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;

@ConfigSerializable
public abstract class OptionalElement {

    protected List<String> shouldShow = Collections.emptyList();

    /**
     * This should only be called after placeholders have been resolved, and ideally only called once.
     */
    public boolean show() {
        return shouldShow.stream().allMatch(s -> ParseUtils.getBoolean(s, false));
    }
}
