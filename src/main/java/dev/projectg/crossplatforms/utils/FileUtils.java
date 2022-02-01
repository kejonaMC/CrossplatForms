package dev.projectg.crossplatforms.utils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    private FileUtils () {
    }

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

        if (!file.getParentFile().mkdirs()) {
            throw new IOException("Failed to make parent directories for file: " + file.getCanonicalPath());
        }
        if (!file.createNewFile()) {
            throw new IllegalStateException("Resource already exists at: " + file.getCanonicalPath());
        }
        FileOutputStream output = new FileOutputStream(file);
        input.transferTo(output);
        output.close();
        input.close();
        return file;
    }
}
