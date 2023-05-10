package dev.kejona.crossplatforms.util;

import dev.kejona.crossplatforms.utils.ConfigurateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigurateUtilsTest {

    private static final String MESSAGE = "[23:57:30 ERROR]: [CForms] [forms, servers, buttons, 0, actions, 2] of type java.util.List<dev.kejona.crossplatforms.action.Action<? super dev.kejona.crossplatforms.interfacing.bedrock.simple.SimpleBedrockForm>>: Unsupported type 'close'. The type is registered but is not compatible with dev.kejona.crossplatforms.action.Action<? super dev.kejona.crossplatforms.interfacing.bedrock.simple.SimpleBedrockForm>. Possible type options are: [server, form, message, commands, transfer_packet]";
    private static final String STRIPPED_MESSAGE = "[23:57:30 ERROR]: [CForms] [forms, servers, buttons, 0, actions, 2] of type List<Action<? super SimpleBedrockForm>>: Unsupported type 'close'. The type is registered but is not compatible with Action<? super SimpleBedrockForm>. Possible type options are: [server, form, message, commands, transfer_packet]";

    @Test
    public void testStripPackages() {
        Assertions.assertEquals("A List", ConfigurateUtils.stripPackageNames("A java.util.List"));
        Assertions.assertEquals("Collections is nice", ConfigurateUtils.stripPackageNames("java.util.Collections is nice"));
        Assertions.assertEquals("List and Map", ConfigurateUtils.stripPackageNames("java.util.List and java.util.Map"));
        Assertions.assertEquals(STRIPPED_MESSAGE, ConfigurateUtils.stripPackageNames(MESSAGE));
    }
}
