package dev.kejona.crossplatforms.utils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;

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
     * @param resourceFileName The resource file as a string
     * @return The file, if it already exists, or copied.
     */
    public static File fileOrCopiedFromResource(File file, String resourceFileName) throws IOException {
        if (file.exists()) {
            return file;
        }

        InputStream input = getResource(resourceFileName);
        if (input == null) {
            throw new AssertionError("Resource " + resourceFileName + " does not exist (" + file + ")");
        }

        file.getParentFile().mkdirs();
        if (!file.createNewFile()) {
            throw new IllegalStateException("Resource already exists at: " + file.getCanonicalPath());
        }
        Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        input.close();
        return file;
    }

    public static Properties getProperties(String resource) throws IOException {
        Properties properties = new Properties();
        properties.load(FileUtils.getResource(resource));
        return properties;
    }

    public static void recursivelyDelete(Path directory) throws IOException {
        if (!Files.isDirectory(directory)) {
            return;
        }

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                if (e != null) {
                    throw e;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
