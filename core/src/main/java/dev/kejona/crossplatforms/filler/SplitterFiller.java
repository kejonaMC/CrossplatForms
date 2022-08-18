package dev.kejona.crossplatforms.filler;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.serialize.TypeResolver;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Stream;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SplitterFiller extends UniversalFiller {

    private static final String TYPE = "splitter";

    @Required
    private String split;
    private String regex = " ";

    @Inject
    private SplitterFiller() {

    }

    @Nonnull
    @Override
    public Stream<String> rawOptions(Resolver resolver) {
        return Arrays.stream(resolver.apply(split).split(regex, 0));
    }

    @Override
    public String type() {
        return TYPE;
    }

    public static void register(FillerSerializer serializer) {
        serializer.filler(TYPE, SplitterFiller.class, typeResolver());
    }

    private static TypeResolver typeResolver() {
        return node -> {
            if (node.node("split").getString() != null) {
                return TYPE;
            } else {
                return null;
            }
        };
    }
}
