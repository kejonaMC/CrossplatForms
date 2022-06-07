package dev.projectg.crossplatforms.spigot.common;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class GeyserHubConverter {

    private static final TypeToken<Map<Integer, ConfigurationNode>> INT_MAP = new TypeToken<Map<Integer, ConfigurationNode>>() {};
    private static final TypeToken<Map<String, ConfigurationNode>> STRING_MAP = new TypeToken<Map<String, ConfigurationNode>>() {};

    private static final YamlConfigurationLoader.Builder LOADER_BUILDER = YamlConfigurationLoader
        .builder()
        .indent(2)
        .nodeStyle(NodeStyle.BLOCK);

    public static Path convert(Path selectorConfig) throws IOException {
        Path parent = selectorConfig.getParent();
        Path converted = parent.resolve("converted"); // directory to place converted files

        ConfigurationNode source = YamlConfigurationLoader.builder() // GeyserHub config
            .file(selectorConfig.toFile())
            .build()
            .load();

        ConfigurationNode version = source.node("Config-Version");
        if (version.virtual() || version.getInt(-1) != 2) {
            Logger.get().warn("Failed to convert " + selectorConfig.getFileName() + " from GeyserHub because its Config-Version is not 2. Update it within the latest version of GeyserHub.");
        }

        YamlConfigurationLoader itemLoader = loader(converted, "access-items.yml");
        ConfigurationNode items = itemLoader.load();
        convertItems(source, items);
        itemLoader.save(items);

        YamlConfigurationLoader menuLoader = loader(converted, "java-menus.yml");
        ConfigurationNode menus = menuLoader.load();
        convertMenus(source, menus);
        menuLoader.save(menus);

        YamlConfigurationLoader formLoader = loader(converted, "bedrock-forms.yml");
        ConfigurationNode forms = formLoader.load();
        convertForms(source, forms);
        formLoader.save(forms);

        Logger.get().info("Converted " + selectorConfig + " from GeyserHub into configs compatible with CrossplatForms in " + converted.toAbsolutePath());
        return converted;
    }

    private static void convertItems(ConfigurationNode selectorConfig, ConfigurationNode target) throws SerializationException {
        ConfigurationNode root = selectorConfig.node("Access-Items");
        target.node("enable").set(root.node("Enable").getBoolean());

        ConfigurationNode targetItems = target.node("items");
        for (ConfigurationNode item : root.node("Items").childrenMap().values()) {
            ConfigurationNode targetItem = targetItems.node(item.key());

            copy(item, "Material", String.class, targetItem, "material", "COMPASS");
            copy(item, "Name", String.class, targetItem, "display-name", "");
            copy(item, "Lore", new TypeToken<List<String>>() {}, targetItem, "lore");
            copy(item, "Slot", int.class, targetItem, "slot", 0);
            copy(item, "Join", boolean.class, targetItem, "on-join", true);
            copy(item, "Respawn", boolean.class, targetItem, "on-respawn", true);

            ConfigurationNode permissions = targetItem.node("permissions");
            permissions.node("DROP").set(item.node("Allow-Drop").getBoolean(false) ? PermissionDefault.TRUE : PermissionDefault.FALSE);
            permissions.node("PRESERVE").set(item.node("Destroy-Dropped").getBoolean(true) ? PermissionDefault.FALSE : PermissionDefault.TRUE);
            permissions.node("MOVE").set(item.node("Allow-Move").getBoolean(false) ? PermissionDefault.TRUE : PermissionDefault.FALSE);

            ConfigurationNode form = item.node("Form");
            if (!form.virtual()) {
                targetItem.node("actions", "form").set(String.class, form.getString());
            }
        }

        target.node("config-version").set(int.class, 3);
    }

    private static void convertMenus(ConfigurationNode selectorConfig, ConfigurationNode target) throws SerializationException {
        ConfigurationNode root = selectorConfig.node("Java-Selector");
        target.node("enable").set(root.node("Enable").getBoolean());

        ConfigurationNode targetMenus = target.node("menus");
        for (ConfigurationNode menu : root.node("Menus").childrenMap().values()) {
            ConfigurationNode targetMenu = targetMenus.node(menu.key());

            copy(menu, "Title", String.class, targetMenu, "title", "");
            copy(menu, "Size", int.class, targetMenu, "size", 5);

            ConfigurationNode buttons = menu.node("Buttons");
            ConfigurationNode targetButtons = targetMenu.node("buttons");
            for (int slot : buttons.get(INT_MAP, Collections.emptyMap()).keySet()) {
                ConfigurationNode button = buttons.node(slot);
                ConfigurationNode targetButton = targetButtons.node(slot);

                copy(button, "Display-Name", String.class, targetButton, "display-name", "");
                copy(button, "Material", String.class, targetButton, "material", "STONE");
                copy(button, "Lore", new TypeToken<List<String>>() {}, targetButton, "lore");

                ConfigurationNode rightClick = button.node("Right-Click");
                if (!rightClick.virtual()) {
                    ConfigurationNode targetRight = targetButton.node("right-click");
                    copy(rightClick, "Commands", new TypeToken<List<String>>() {}, targetRight, "commands");
                    copy(rightClick, "Server", String.class, targetRight, "server");
                }

                ConfigurationNode leftClick = button.node("Left-Click");
                if (!leftClick.virtual()) {
                    ConfigurationNode targetLeft = targetButton.node("left-click");
                    copy(leftClick, "Commands", new TypeToken<List<String>>() {}, targetLeft, "commands");
                    copy(leftClick, "Server", String.class, targetLeft, "server");
                }

                ConfigurationNode anyClick = button.node("Any-Click");
                if (!anyClick.virtual()) {
                    ConfigurationNode targetAny = targetButton.node("any-click");
                    copy(anyClick, "Commands", new TypeToken<List<String>>() {}, targetAny, "commands");
                    copy(anyClick, "Server", String.class, targetAny, "server");
                }
            }
        }

        target.node("config-version").set(int.class, 1);
    }

    private static void convertForms(ConfigurationNode selectorConfig, ConfigurationNode target) throws SerializationException {
        ConfigurationNode root = selectorConfig.node("Bedrock-Selector");
        target.node("enable").set(root.node("Enable").getBoolean());

        ConfigurationNode targetForms = target.node("forms");
        for (ConfigurationNode form : root.node("Forms").childrenMap().values()) {
            ConfigurationNode targetForm = targetForms.node(form.key());

            targetForm.node("type").set("simple_form");
            copy(form, "Title", String.class, targetForm, "title");
            copy(form, "Content", String.class, targetForm, "content");

            ConfigurationNode buttons = form.node("Buttons");
            ConfigurationNode targetButtons = targetForm.node("buttons");
            for (String identifier : buttons.get(STRING_MAP, Collections.emptyMap()).keySet()) {
                ConfigurationNode button = buttons.node(identifier);
                ConfigurationNode targetButton = targetButtons.appendListNode();

                copy(button, "Button-Text", String.class, targetButton, "text", "");
                copy(button, "ImageURL", String.class, targetButton, "image");

                ConfigurationNode targetActions = targetButton.node("actions");

                ConfigurationNode commands = button.node("Commands");
                if (!commands.virtual()) {
                    targetActions.node("commands").set(commands.get(new TypeToken<List<String>>() {}));
                }
                ConfigurationNode server = button.node("Server");
                if (!server.virtual()) {
                    targetActions.node("server").set(server.getString());
                }
            }
        }

        target.node("config-version").set(int.class, 4);
    }

    public static YamlConfigurationLoader loader(Path folder, String fileName) {
        return LOADER_BUILDER.file(folder.resolve(fileName).toFile()).build();
    }

    private static <T> void copy(ConfigurationNode source,
                                 String sKey,
                                 Class<T> type,
                                 ConfigurationNode target,
                                 String tKey,
                                 @Nullable T def) throws SerializationException {

        target.node(tKey).set(source.node(sKey).get(type, def));
    }

    private static <T> void copy(ConfigurationNode source,
                                 String sKey,
                                 Class<T> type,
                                 ConfigurationNode target,
                                 String tKey) throws SerializationException {

        target.node(tKey).set(source.node(sKey).get(type));
    }

    private static <T> void copy(ConfigurationNode source,
                                 String sKey,
                                 TypeToken<T> type,
                                 ConfigurationNode target,
                                 String tKey,
                                 @Nullable T def) throws SerializationException {

        target.node(tKey).set(source.node(sKey).get(type, def));
    }

    private static <T> void copy(ConfigurationNode source,
                                 String sKey,
                                 TypeToken<T> type,
                                 ConfigurationNode target,
                                 String tKey) throws SerializationException {

        target.node(tKey).set(source.node(sKey).get(type));
    }

    private GeyserHubConverter() {

    }
}
