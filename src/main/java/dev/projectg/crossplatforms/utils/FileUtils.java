package dev.projectg.crossplatforms.utils;

import javax.annotation.Nullable;
import java.io.InputStream;

public class FileUtils {

    @Nullable
    public static InputStream getResource(String resource) {
        return FileUtils.class.getClassLoader().getResourceAsStream(resource);
    }
}
