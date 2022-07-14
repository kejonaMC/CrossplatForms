package dev.kejona.crossplatforms.interfacing.bedrock;

import dev.kejona.crossplatforms.utils.ParseUtils;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nullable;

@ConfigSerializable
public abstract class OptionalElement {

    @Nullable
    protected String shouldShow = null;

    /**
     * This should only be called after placeholders have been resolved, and ideally only called once.
     */
    public boolean show() {
        if (shouldShow == null) {
            return true;
        } else {
            return ParseUtils.getBoolean(shouldShow, true);
        }
    }
}
