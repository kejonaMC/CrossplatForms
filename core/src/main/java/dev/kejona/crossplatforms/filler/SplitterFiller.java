package dev.kejona.crossplatforms.filler;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Arrays;
import java.util.Collection;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SplitterFiller extends Filler {

    public static final String TYPE = "splitter";

    @Required
    private String value;

    private String regex = " ";

    @Override
    public Collection<String> generate() {
        return Arrays.asList(value.split(regex, 0));
    }

    @Override
    public String type() {
        return TYPE;
    }
}
