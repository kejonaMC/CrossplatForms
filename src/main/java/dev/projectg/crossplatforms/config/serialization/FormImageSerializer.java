package dev.projectg.crossplatforms.config.serialization;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.cumulus.util.FormImage;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class FormImageSerializer implements TypeSerializer<FormImage> {

    @Override
    public FormImage deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String location = node.getString();
        if (location == null) {
            throw new SerializationException("Button image (web link or file location) is null (not given) at " + node.path());
        }

        if (location.startsWith("http://") || location.startsWith("https://")) {
            return FormImage.of(FormImage.Type.URL, location);
        } else if (Files.isRegularFile(Path.of(location))) {
            return FormImage.of(FormImage.Type.PATH, location);
        } else {
            throw new SerializationException("Failed to determine if button image was a web link or file location at " + node.path());
        }
    }

    @Override
    public void serialize(Type type, @Nullable FormImage image, ConfigurationNode node) throws SerializationException {
        if (image == null) {
            node.raw(null);
            return;
        }
        node.set(String.class, image.getData());
    }
}
