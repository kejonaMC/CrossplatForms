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
        return fileOrCopiedFromResource(file, file.getName());
    }

    /**
     * @param file The file location that should be checked, and where the resource should be copied to if it doesn't exist
     * @param resourceFile The resource file as a string
     * @return The file, if it already exists, or copied.
     */
    public static File fileOrCopiedFromResource(File file, String resourceFile) throws IOException {
        if (file.exists()) {
            return file;
        }

        InputStream input = getResource(resourceFile);
        if (input == null) {
            throw new AssertionError("Resource " + resourceFile + " does not exist (" + file + ")");
        }

        file.getParentFile().mkdirs();
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
