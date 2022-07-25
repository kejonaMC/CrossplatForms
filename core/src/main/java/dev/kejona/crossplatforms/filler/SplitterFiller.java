package dev.kejona.crossplatforms.filler;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Resolver;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Stream;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SplitterFiller extends UniversalFiller {

    public static final String TYPE = "splitter";

    @Required
    private String value;
    private String regex = " ";

    @Inject
    private SplitterFiller() {

    }

    @Nonnull
    @Override
    public Stream<String> rawOptions(Resolver resolver) {
        return Arrays.stream(resolver.apply(value).split(regex, 0));
    }

    @Override
    public String type() {
        return TYPE;
    }
}
