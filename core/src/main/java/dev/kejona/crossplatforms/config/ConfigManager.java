package dev.kejona.crossplatforms.config;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.action.ActionSerializer;
import dev.kejona.crossplatforms.action.CommandsAction;
import dev.kejona.crossplatforms.action.InterfaceAction;
import dev.kejona.crossplatforms.action.MessageAction;
import dev.kejona.crossplatforms.command.DispatchableCommand;
import dev.kejona.crossplatforms.command.DispatchableCommandSerializer;
import dev.kejona.crossplatforms.command.custom.Literals;
import dev.kejona.crossplatforms.command.custom.CustomCommand;
import dev.kejona.crossplatforms.command.custom.CustomCommandSerializer;
import dev.kejona.crossplatforms.filler.FillerSerializer;
import dev.kejona.crossplatforms.filler.PlayerFiller;
import dev.kejona.crossplatforms.filler.SplitterFiller;
import dev.kejona.crossplatforms.interfacing.Argument;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.Option;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.OptionSerializer;
import dev.kejona.crossplatforms.item.SkullProfile;
import dev.kejona.crossplatforms.parser.Parser;
import dev.kejona.crossplatforms.parser.ParserSerializer;
import dev.kejona.crossplatforms.serialize.PathNodeResolver;
import dev.kejona.crossplatforms.serialize.StreamSerializer;
import dev.kejona.crossplatforms.serialize.UnaryNodes;
import dev.kejona.crossplatforms.utils.ConfigurateUtils;
import dev.kejona.crossplatforms.utils.FileUtils;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.guice.GuiceObjectMapperProvider;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ConfigManager {

    private final Path directory;
    private final Logger logger;

    private final YamlConfigurationLoader.Builder loaderBuilder;
    private final List<ConfigId> identifiers = new ArrayList<>();

    // todo: support using the same config class for two different configs
    private final Map<Class<? extends Configuration>, Configuration> configurations = new HashMap<>();
    private final Map<Class<? extends Configuration>, ConfigurationNode> nodes = new HashMap<>();

    @Getter
    private final ActionSerializer actionSerializer;

    @Getter
    private final FillerSerializer fillerSerializer = new FillerSerializer();

    public ConfigManager(Path directory, Logger logger, Injector injector) {
        this.directory = directory;
        this.logger = logger;
        actionSerializer = new ActionSerializer();

        ObjectMapper.Factory mapperFactory = ObjectMapper
                .factoryBuilder()
                .addDiscoverer(GuiceObjectMapperProvider.injectedObjectDiscoverer(injector))
                .addNodeResolver(PathNodeResolver.nodePath())
                .addPostProcessor(new UnaryNodes.ProcessorFactory())
                .build();
        loaderBuilder = YamlConfigurationLoader.builder();
        loaderBuilder.defaultOptions(opts -> {
            // If certain type serializers should be registered with less priority, they should be registered in a new
            // #serializers call before the normal call. They are then registered to a TypeSerializerCollection
            // that is a parent (less priority) of the normal type serializers registered afterward. e.g:
            //opts = opts.serializers(builder -> builder.registerAnnotatedObjects(mapperFactory));

            return opts.serializers(builder -> {
                // register the object mapper in the same type serializer collection
                builder.registerAnnotatedObjects(mapperFactory);

                // serializers for classes not suitable for object mapping (scalars, etc)
                builder.register(StreamSerializer.TYPE, new StreamSerializer());
                builder.registerExact(Argument.class, new Argument.Serializer());
                builder.registerExact(Option.class, new OptionSerializer());
                builder.registerExact(Literals.class, new Literals.Serializer());
                builder.registerExact(SkullProfile.class, new SkullProfile.Serializer());

                // serializers for abstract classes
                builder.registerExact(CustomCommand.class, new CustomCommandSerializer());
                builder.register(DispatchableCommand.class, new DispatchableCommandSerializer());
                builder.registerExact(Parser.class, new ParserSerializer());

                // register actions to the action serializer
                InterfaceAction.register(actionSerializer);
                CommandsAction.register(actionSerializer);
                MessageAction.register(actionSerializer);
                // register the serializer to the collection
                actionSerializer.register(builder);

                // register fillers
                PlayerFiller.register(fillerSerializer);
                SplitterFiller.register(fillerSerializer);
                fillerSerializer.register(builder);
            });
        });
        // don't initialize default values for object values
        // default parameters provided to ConfigurationNode getter methods should not be set to the node
        loaderBuilder.defaultOptions(opts -> opts.implicitInitialization(false).shouldCopyDefaults(false));
        loaderBuilder.nodeStyle(NodeStyle.BLOCK); // don't inline lists, maps, etc
        loaderBuilder.indent(2);
    }

    public void register(ConfigId id) {
        identifiers.add(id);
    }

    public void registerPriority(ConfigId id) {
        identifiers.add(0, id);
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
            } catch (IOException | ConfigurationException e) {
                logger.severe("Failed to load configuration " + configId.file);
                String message = e.getMessage();
                if (logger.isDebug() || configId.equals(ConfigId.GENERAL) || message.contains("Unknown error")) {
                    // if the config failing to load is config.yml, then its impossible to enable debug and see the full error.
                    // message is useless on its own if unknown
                    e.printStackTrace();
                } else {
                    logger.severe("Enable debug mode for further information.");
                    logger.severe(ConfigurateUtils.stripPackageNames(message));
                }
                if (!useMinimalDefaults(configId)) {
                    return false;
                }
            }

            if (configId.postProcessor != null) {
                Configuration config = configurations.get(configId.clazz);
                if (config == null) {
                    logger.severe("Expected " + configId.file + " to be loaded but it was not present");
                    logger.debugStack();
                    return false;
                } else {
                    configId.postProcessor.accept(config);
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
    private boolean loadConfig(ConfigId config) throws IOException, ConfigurationException {
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
