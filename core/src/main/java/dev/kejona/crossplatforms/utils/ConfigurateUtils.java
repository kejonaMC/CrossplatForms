package dev.kejona.crossplatforms.utils;

import com.google.inject.Key;
import dev.kejona.crossplatforms.action.Action;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConfigurateUtils {

    public static final Pattern CLASS_NAME_PATTERN = Pattern.compile("([A-Za-z0-9_]+\\.)+([A-Z][A-Za-z0-9_]+)");
    public static final TypeToken<Map<String, ConfigurationNode>> NODE_MAP = new TypeToken<Map<String, ConfigurationNode>>() {};

    public static final TransformAction ACTION_TRANSLATOR = (path, node) -> {
        translateActions(node);
        return null; // don't move node
    };

    private ConfigurateUtils() {
        // util class
    }

    public static String stripPackageNames(String message) {
        final Matcher matcher = CLASS_NAME_PATTERN.matcher(message);
        final StringBuilder stripped = new StringBuilder();
        int lastMatch = 0;
        while (matcher.find()) {
            // add everything from the last match to the start of this matched package name
            stripped.append(message, lastMatch, matcher.start());
            // update lastMatch to the end of this simple class name and then add the simple class name
            lastMatch = matcher.end(2);
            stripped.append(message, matcher.start(2), lastMatch);
        }
        stripped.append(message, lastMatch, matcher.regionEnd()); // add every trailing after the final match
        return stripped.toString();
    }

    public static boolean isListOrScalar(ConfigurationNode node, String childKey) {
        ConfigurationNode child = node.node(childKey);
        if (child.isNull()) {
            return false;
        }

        return child.isList() || !child.isMap();
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

    public static void transformChildren(ConfigurationTransformation.Builder builder,
                                         NodePath path,
                                         TransformAction action,
                                         Object... children) {
        for (Object child : children) {
            builder.addAction(path.withAppendedChild(child), action);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Key<T> keyFromToken(TypeToken<T> token) {
        return (Key<T>) Key.get(token.getType());
    }

    /**
     * Translates an actions node from the old map type to the new list type.
     */
    public static void translateActions(ConfigurationNode actions) throws SerializationException {
        if (actions.virtual()) {
            throw new IllegalArgumentException("actions is virtual");
        }
        if (!actions.isMap()) {
            throw new IllegalArgumentException("actions is not a map");
        }
        Map<String, ConfigurationNode> oldChildren = actions.get(NODE_MAP);
        actions.raw(null); // clear it
        if (oldChildren == null) {
            throw new SerializationException("Map of children was deserialized null");
        }

        for (String type : oldChildren.keySet()) {
            if (type.equals("close")) {
                // this was a SimpleAction where the string value didn't matter.
                actions.appendListNode().node("type").set(type);
                continue;
            }

            ConfigurationNode actionDefinition = oldChildren.get(type);
            if (actionDefinition.isMap()) {
                if (!Action.typeInferrable(type)) {
                    actionDefinition.node("type").set(type);
                }
                actions.appendListNode().set(actionDefinition);
            } else {
                // actionDefinition is a scalar or list (the value of a SimpleType action).
                // it must be added to the entry on a manually added key.
                ConfigurationNode entry = actions.appendListNode();
                entry.node(type).set(actionDefinition);
                if (!Action.typeInferrable(type)) {
                    entry.node("type").set(type);
                }
            }
        }
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
