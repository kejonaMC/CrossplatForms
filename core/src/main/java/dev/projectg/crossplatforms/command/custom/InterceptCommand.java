package dev.projectg.crossplatforms.command.custom;

import dev.projectg.crossplatforms.command.CommandType;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
public class InterceptCommand extends CustomCommand {

    public static final String INTERCEPT_PASS_TYPE = CommandType.INTERCEPT_PASS.name().toLowerCase(Locale.ROOT);
    public static final String INTERCEPT_CANCEL_TYPE = CommandType.INTERCEPT_PASS.name().toLowerCase(Locale.ROOT);

    protected static final String PATTERN_KEY = "pattern";
    protected static final String EXACT_KEY = "exact";

    @Nullable
    private final Pattern pattern;

    @Nullable
    private final String exact;

    public InterceptCommand(@Nonnull Pattern pattern) {
        this.pattern = Objects.requireNonNull(pattern);
        this.exact = null;
    }

    public InterceptCommand(@Nonnull String exact) {
        this.pattern = null;
        this.exact = Objects.requireNonNull(exact);
    }

    @Override
    public String type() {
        return getMethod().name().toLowerCase(Locale.ROOT);
        // either "intercept_pass" or "intercept_cancel"
    }
}
