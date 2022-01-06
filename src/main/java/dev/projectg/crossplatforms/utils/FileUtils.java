package dev.projectg.crossplatforms.utils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    @Nullable
    public static InputStream getResource(String resource) {
        return FileUtils.class.getClassLoader().getResourceAsStream(resource);
    }

    public static File fileOrCopiedFromResource(File file) throws IOException {
        if (file.exists()) {
            return file;
        }

        String name = file.getName();
        InputStream input = getResource(name);
        if (input == null) {
            throw new AssertionError("Resource " + name + " does not exist.");
        }

        file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream output = new FileOutputStream(file);
        output.write(input.readAllBytes());
        output.close();
        input.close();
        return file;
    }
}
