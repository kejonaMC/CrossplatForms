package dev.kejona.crossplatforms.filler;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.serialize.ValuedType;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public final List<String> generate() {
        return generateRaw().collect(Collectors.toList());
    }

    protected abstract Stream<String> generateRaw();
}
