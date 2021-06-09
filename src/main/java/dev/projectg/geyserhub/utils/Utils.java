package dev.projectg.geyserhub.utils;

import java.io.InputStream;

public class Utils {

    public static InputStream getResource(String resource) {
        InputStream stream = Utils.class.getClassLoader().getResourceAsStream(resource);
        if (stream == null) {
            throw new AssertionError("Unable to find resource: " + resource);
        }
        return stream;
    }
}
