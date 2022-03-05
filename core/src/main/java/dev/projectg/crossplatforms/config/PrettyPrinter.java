package dev.projectg.crossplatforms.config;

import org.spongepowered.configurate.ConfigurationNode;

import java.util.Collection;

/**
 * Constructs printable, user-friendly text from {@link ConfigurationNode}s
 */
public class PrettyPrinter {

    private final int indentLevel;
    private final String indent;

    /**
     * Creates a PrettyPrinter with a default indentation level of 2.
     */
    public PrettyPrinter() {
        this.indentLevel = 2;
        this.indent = "  ";
    }

    /**
     * Creates a PrettyPrinter
     * @param indent The indent level to use for indentation.
     */
    public PrettyPrinter(int indent) {
        this.indentLevel = indent;
        this.indent = " ".repeat(indentLevel);
    }

    /**
     * Get the ConfigurationNode and its children as a viewable text
     * @param node The node to generate text from
     * @param showKey Whether or not to include the key of the {@link ConfigurationNode} in the text.
     * @return The viewable text
     */
    public String pretty(ConfigurationNode node, boolean showKey) {
        return pretty(node, 0, showKey);
    }

    /**
     * Get the ConfigurationNode and its children as viewable text. If the {@link ConfigurationNode} has no parent
     * (its key is null), the key is omitted and its value is printed at the base.
     * @param node The node to generate text from
     * @return The viewable text
     */
    public String pretty(ConfigurationNode node) {
        return pretty(node, 0, node.parent() != null);
    }

    /**
     * @param node The node to generate text from
     * @param indent The indent level to print at
     * @param showKey Whether or not to include the key of the node.
     * @return The text
     */
    private String pretty(ConfigurationNode node, int indent, boolean showKey) {
        String prefix;
        if (showKey) {
            prefix = node.key() + ": ";
        } else {
            prefix = "";
        }

        if (node.virtual()) {
            return prefix;
        } else if (node.isMap()) {
            return children(node, node.childrenMap().values(), indent, showKey);
        } else if (node.isList()) {
            return children(node, node.childrenList(), indent, showKey);
        } else {
            return prefix + fromRaw(node.raw());
        }
    }

    /**
     * Generate text from a node and its children (for map-like or list-like nodes)
     * @param node The node to generate text from
     * @param children The children of the node
     * @param indent The indent level to print at
     * @param key Whether or not to include the key of the node in the text. If the key is included, it is included at
     *            the provided indentation level, and the children are listed a level lower. If not, the children are
     *            directly listed at the given indentation level.
     * @return The text
     */
    private String children(ConfigurationNode node, Collection<? extends ConfigurationNode> children, int indent, boolean key) {
        StringBuilder builder = new StringBuilder();
        int childIndent;
        if (key) {
            childIndent = indent + 1;
            builder.append(indent(indent));
            builder.append(node.key()).append(":\n");
        } else {
            childIndent = indent;
        }
        for (ConfigurationNode child : children) {
            builder.append(indent(childIndent));
            builder.append(pretty(child, childIndent, true)).append("\n");
        }
        return builder.toString().trim(); // Not really sure why the trim is necessary, but if not we get blank lines following
    }

    private String indent(int level) {
        return indent.repeat(level);
    }

    private String fromRaw(Object raw) {
        if (raw == null) {
            return "";
        } else {
            return raw.toString();
        }
    }
}
