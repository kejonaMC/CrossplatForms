package dev.kejona.crossplatforms.interfacing.bedrock;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.utils.ParseUtils;
import dev.kejona.crossplatforms.utils.StringUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@ConfigSerializable
public abstract class OptionalElement {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private static final Predicate<String> MATCHER = s -> ParseUtils.getBoolean(s, () -> defaultShow(s));

    protected List<String> shouldShow = Collections.emptyList();
    protected boolean stripFormatting = false;
    protected Mode mode = Mode.AND;

    /**
     * This should only be called after placeholders have been resolved, and ideally only called once.
     */
    @Contract(pure = true)
    public boolean show() {
        return show(shouldShow.stream());
    }

    /**
     * Determines if this should be shown based off the given resolved expressions
     * @param booleans a list of strings that can be coerced to booleans. If {@link OptionalElement#stripFormatting} is true,
     *                 the strings will be stripped of any text formatting
     * @return true if this should be shown
     */
    @Contract(pure = true)
    protected boolean show(Stream<String> booleans) {
        Stream<String> stream = booleans;
        if (stripFormatting) {
            stream = stream.map(OptionalElement::toPlain);
        }

        if (mode == Mode.AND) {
            return stream.allMatch(MATCHER);
        } else {
            return stream.anyMatch(MATCHER);
        }
    }

    private static boolean defaultShow(String failed) {
        Logger.get().warn("Boolean coercion failed for should-show, defaulting to false: " + failed);
        return false;
    }

    private static String toPlain(String text) {
        if (StringUtils.hasChar(text, LegacyComponentSerializer.SECTION_CHAR)) {
            return PLAIN.serialize(LEGACY.deserialize(text));
        }
        return text;
    }

    enum Mode {
        AND,
        OR
    }
}
