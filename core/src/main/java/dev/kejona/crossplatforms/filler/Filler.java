package dev.kejona.crossplatforms.filler;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.serialize.ValuedType;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Collection;

@Accessors(fluent = true)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public abstract class Filler implements ValuedType {

    @Getter
    @Setting("before")
    private boolean insertBefore = false;

    @Inject
    protected Filler() {

    }

    public abstract Collection<String> generate();
}
