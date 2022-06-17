package dev.kejona.crossplatforms.utils;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

public class ConfigurateUtils {

    private ConfigurateUtils() {
        // util class
    }

    /**
     * Move specific children nodes of a parent node under a different parent node beneath the original.
     * @param parent The parent node to get the children of.
     * @param childMatcher A matcher based off the key of each child to determine whether the child node should be moved.
     * @param destination The path of the sub-parent relative to the given original parent.
     */
    public static void moveChildren(ConfigurationNode parent,
                                    Predicate<Object> childMatcher,
                                    Object... destination) throws SerializationException {

        Map<Object, ? extends ConfigurationNode> children = parent.childrenMap();
        for (Object obj : children.keySet()) {
            if (childMatcher.test(obj)) {
                parent.node(destination).node(obj).set(children.get(obj));
                parent.node(obj).raw(null);
            }
        }
        // todo: unit tests
    }

    public static YamlConfigurationLoader.Builder loaderBuilder(File file) {
        YamlConfigurationLoader.Builder loaderBuilder = YamlConfigurationLoader.builder();
        loaderBuilder.defaultOptions(opts -> opts.implicitInitialization(false).shouldCopyDefaults(false));
        loaderBuilder.nodeStyle(NodeStyle.BLOCK);
        loaderBuilder.indent(2);
        loaderBuilder.file(file);
        return loaderBuilder;
    }

    public static YamlConfigurationLoader.Builder loaderBuilder(File directory, String resource) throws IOException {
        return loaderBuilder(FileUtils.fileOrCopiedFromResource(new File(directory, resource)));
    }
}
