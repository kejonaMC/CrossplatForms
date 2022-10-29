package dev.kejona.crossplatforms.filler;

import dev.kejona.crossplatforms.IllegalValueException;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.TriggerException;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.serialize.AsNodePath;
import dev.kejona.crossplatforms.utils.ParseUtils;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RangeFiller extends UniversalFiller {

    private static final String TYPE = "range";

    @AsNodePath
    private String node;

    private String start = "0";
    private String step = "1";

    @Required
    private String end;

    private String format;

    @Nonnull
    @Override
    public Stream<String> rawOptions(Resolver resolver) {
        int start, step, end;
        try {
            start = ParseUtils.getInt(this.start, "start");
            step = ParseUtils.getInt(this.step, "step");
            end  = ParseUtils.getInt(this.end, "end");
        } catch (IllegalValueException e) {
            Logger logger = Logger.get();



            Logger.get().severe("Failed to parse " + e.identifier() + " for a 'range' filler, got '" + e.value() + "' instead of a " + e.expectedType());
            return Stream.empty();
        }

        List<String> list = new ArrayList<>();
        if (format == null || format.isEmpty() || format.equals("%i%")) {
            for (int i = start; i < end; i += step) {
                list.add(Integer.toString(i));
            }
        } else {
            for (int i = start; i < end; i += step) {
                list.add(format.replace("%i%", Integer.toString(i)));
            }
        }

        return list.stream();
    }

    @Override
    public String type() {
        return TYPE;
    }
}
