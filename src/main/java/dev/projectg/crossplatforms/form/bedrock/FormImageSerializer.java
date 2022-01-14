package dev.projectg.crossplatforms.form.bedrock;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.cumulus.util.FormImage;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class FormImageSerializer implements TypeSerializer<FormImage> {

    @Override
    public FormImage deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String location = node.getString();
        if (location == null) {
            throw new SerializationException("Button image (web link or file location) is null (not given) at " + node.path());
        }

        FormImage.Type imageType;
        if (location.startsWith("http://") || location.startsWith("https://")) {
            imageType = FormImage.Type.URL;
        } else {
            imageType = FormImage.Type.PATH;
        }

        return FormImage.of(imageType, location);
    }

    @Override
    public void serialize(Type type, @Nullable FormImage image, ConfigurationNode node) throws SerializationException {
        if (image == null) {
            node.raw(null);
            return;
        }
        node.set(FormImage.Type.class, image.getType());
        node.set(String.class, image.getData());
    }
}
