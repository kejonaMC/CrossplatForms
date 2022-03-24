package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.command.DispatchableCommandSerializer;
import dev.projectg.crossplatforms.command.custom.CustomCommand;
import dev.projectg.crossplatforms.command.custom.CustomCommandSerializer;
import dev.projectg.crossplatforms.command.custom.InterceptCommand;
import dev.projectg.crossplatforms.command.custom.InterceptCommandSerializer;
import dev.projectg.crossplatforms.config.serializer.KeyedTypeListSerializer;
import dev.projectg.crossplatforms.config.serializer.KeyedTypeSerializer;
import dev.projectg.crossplatforms.parser.Parser;
import dev.projectg.crossplatforms.parser.ParserSerializer;
import dev.projectg.crossplatforms.utils.FileUtils;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class ConfigManager {

    private final Path directory;
    private final Logger logger;

    private final YamlConfigurationLoader.Builder loaderBuilder;
    private final Set<ConfigId> identifiers = new HashSet<>();

    // todo: support using the same config class for two different configs
    private final Map<Class<? extends Configuration>, Configuration> configurations = new HashMap<>();
    private final Map<Class<? extends Configuration>, ConfigurationNode> nodes = new HashMap<>();

    @Getter
    private final KeyedTypeSerializer<Action> actionSerializer = new KeyedTypeSerializer<>();

    public ConfigManager(Path directory, Logger logger) {
        this.directory = directory;
        this.logger = logger;
        // type serializers for abstract classes and external library classes
        loaderBuilder = YamlConfigurationLoader.builder();
        loaderBuilder.defaultOptions(opts -> (opts.serializers(builder -> {
            builder.registerExact(CustomCommand.class, new CustomCommandSerializer());
            builder.registerExact(InterceptCommand.class, new InterceptCommandSerializer());
            builder.register(DispatchableCommand.class, new DispatchableCommandSerializer());
            builder.registerExact(Action.class, actionSerializer);
            builder.register(new TypeToken<List<Action>>() {}, new KeyedTypeListSerializer<>(actionSerializer));
            builder.registerExact(Parser.class, new ParserSerializer());
        })));
        // don't initialize default values for object values
        // default parameters provided to ConfigurationNode getter methods should not be set to the node
        loaderBuilder.defaultOptions(opts -> opts.implicitInitialization(false).shouldCopyDefaults(false));
        loaderBuilder.nodeStyle(NodeStyle.BLOCK); // don't inline lists, maps, etc
        loaderBuilder.indent(2);
    }

    public void register(ConfigId id) {
        identifiers.add(id);
    }

    public void serializers(Consumer<TypeSerializerCollection.Builder> builder) {
        loaderBuilder.defaultOptions(opts -> (opts.serializers(builder)));
    }

    @SuppressWarnings("unchecked")
    public <T extends Configuration> Optional<T> getConfig(Class<T> clazz) {
        return Optional.ofNullable((T) configurations.get(clazz));
    }

    /**
     * Get the {@link ConfigurationNode} for a given {@link Configuration} type.
     * @param clazz The class representing the Configuration
     * @param <T> Configuration type
     * @return The configuration node for the given type. This will only return not-null if there was complete success in
     * updating and deserializing the given config. For example, {@link #getConfig(Class)} may return not-null for the
     * same config, but if deserialization failed and it only exists as minimal defaults, this will return false.
     */
    public <T extends Configuration> Optional<ConfigurationNode> getNode(Class<T> clazz) {
        return Optional.ofNullable(nodes.get(clazz));
    }

    /**
     * Load every config in {@link ConfigId}
     * @return false if there was a failure loading any of configurations
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean load() {
        for (ConfigId configId : identifiers) {
            try {
                if (!loadConfig(configId)) {
                    return false;
                }
            } catch (IOException e) {
                logger.severe("Failed to load configuration " + configId.file);
                String message = e.getMessage();
                if (logger.isDebug() || message.contains("Unknown error")) {
                    // message is useless on its own if unknown
                    e.printStackTrace();
                } else {
                    logger.severe("Enable debug mode for further information.");
                    logger.severe(message);
                }
                if (!useMinimalDefaults(configId)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Load a configuration from file. The config will only be loaded into memory if there were zero errors.
     * @param config The configuration to load
     * @return The success state
     */
    private boolean loadConfig(ConfigId config) throws IOException {
        String name = config.file;
        File file = FileUtils.fileOrCopiedFromResource(directory.resolve(config.file).toFile(), config.file);
        YamlConfigurationLoader loader = loaderBuilder.file(file).build();
        ConfigurationNode node = loader.load();

        boolean correctVersion = true;
        if (node.hasChild(Configuration.VERSION_KEY)) { // ensure version is correct
            int currentVersion = node.node(Configuration.VERSION_KEY).getInt();
            if (currentVersion != config.version) {
                if (config.updater == null) {
                    logger.severe(name + " must have a version of " + config.version + " but is at " + currentVersion + ". Please back it up and regenerate a new config.");
                    correctVersion = false;
                } else if (currentVersion < config.minimumVersion || currentVersion > config.version) {
                    logger.severe(name + " must have a version between " + config.minimumVersion + " and " + config.version + " but is at " + currentVersion + ". Please back it up and regenerate a new config.");
                    correctVersion = false;
                } else {
                    ConfigurationNode copy = node.copy(); // keep an old copy to save to file if an update happens
                    ConfigurationTransformation.Versioned updater = config.updater.get(); // transformer for performing updates
                    int startVersion = updater.version(node);
                    updater.apply(node); // update if necessary
                    int endVersion = updater.version(node);
                    if (startVersion != endVersion) {
                        loaderBuilder.file(oldCopy(config)).build().save(copy); // save the old copy
                        loader.save(node); // save the updated version
                    }
                    if (endVersion == config.version) {
                        logger.info("Updated " + name + " from version " + startVersion + " to " + endVersion);
                    } else {
                        logger.severe("Failed to update " + name + " from version " + startVersion + " to " + endVersion);
                        correctVersion = false;
                    }
                }
            } else {
                logger.debug(name + " is at the correct version: " + config.version);
            }
        } else {
            logger.severe(name + " must defined a " + Configuration.VERSION_KEY + ". Please back it up and regenerate a new config.");
            correctVersion = false;
        }

        Configuration mapped;
        if (correctVersion) {
            mapped = node.get(config.clazz); // Map it to the object
            if (mapped == null) {
                logger.severe("Failed to deserialize " + name + " to " + config.clazz + ": Mapped object returned null.");
            }
        } else {
            mapped = null;
        }

        if (mapped == null) {
            return useMinimalDefaults(config);
        } else {
            configurations.put(config.clazz, mapped);
            nodes.put(config.clazz, node);
            return true;
        }
    }

    private boolean useMinimalDefaults(ConfigId config) {
        try {
            // Get the default values so that the plugin can be reloaded at a later time
            configurations.put(config.clazz, config.clazz.getConstructor().newInstance());
            logger.warn("Falling back to MINIMAL DEFAULTS for configuration: " + config.file);
            return true;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.severe("Failed to fallback to defaults for configuration " + config.file + ": " + e.getLocalizedMessage());
            if (logger.isDebug()) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private File oldCopy(ConfigId config) {
        Path configFile = Paths.get(config.file);
        Path parent = configFile.getParent();

        String newName = "old-" + configFile.getFileName();
        if (parent == null) {
            // ConfigId was just defined as a filename
            return directory.resolve(newName).toFile();
        } else {
            // config file is in a sub directory below the main directory
            return directory.resolve(parent).resolve(newName).toFile();
        }
    }
}
