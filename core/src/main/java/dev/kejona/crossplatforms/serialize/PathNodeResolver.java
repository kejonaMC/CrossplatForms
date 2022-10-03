package dev.kejona.crossplatforms.serialize;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.objectmapping.meta.NodeResolver;

import java.lang.reflect.AnnotatedElement;

/**
 * Creates resolvers that provide the {@link NodePath} of the containing node for values.
 * String fields annotated with {@link AsNodePath} will receive the {@link NodePath} of the object containing the field.
 */
public class PathNodeResolver implements NodeResolver.Factory {

    private static final PathNodeResolver INSTANCE = new PathNodeResolver();

    private PathNodeResolver() {

    }

    @Override
    public @Nullable NodeResolver make(String name, AnnotatedElement element) {
        if (element.isAnnotationPresent(AsNodePath.class)) {
            return node -> BasicConfigurationNode.root(node.options()).raw(node.path().toString());
        }
        return null;
    }

    public static NodeResolver.Factory nodePath() {
        return INSTANCE;
    }
}
