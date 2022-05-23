package dev.projectg.crossplatforms.command.custom;

import dev.projectg.crossplatforms.command.CommandType;
import lombok.Getter;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Locale;
import java.util.regex.Pattern;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class InterceptCommand extends CustomCommand {

    public static final String INTERCEPT_PASS_TYPE = CommandType.INTERCEPT_PASS.name().toLowerCase(Locale.ROOT);
    public static final String INTERCEPT_CANCEL_TYPE = CommandType.INTERCEPT_CANCEL.name().toLowerCase(Locale.ROOT);

    @Nullable
    private Pattern pattern;

    @Nullable
    private String exact;

    @Nullable
    @Override
    public String getPermission() {
        return super.getPermission();
    }

    @Override
    public String type() {
        return getMethod().name().toLowerCase(Locale.ROOT);
        // either "intercept_pass" or "intercept_cancel"
    }
}
