package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.form.bedrock.BedrockForm;
import dev.projectg.crossplatforms.form.bedrock.BedrockFormSerializer;
import dev.projectg.crossplatforms.form.bedrock.custom.CustomComponent;
import dev.projectg.crossplatforms.form.bedrock.custom.ComponentSerializer;
import dev.projectg.crossplatforms.form.bedrock.FormImageSerializer;
import dev.projectg.crossplatforms.utils.FileUtils;
import org.geysermc.cumulus.util.FormImage;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final YamlConfigurationLoader.Builder loaderBuilder;
    private final Map<Class<? extends Configuration>, Configuration> configurations = new HashMap<>();
    private final File directory;
    private final Logger logger;

    public ConfigManager(File directory, Logger logger) {
        this.directory = directory;
        this.logger = logger;

        loaderBuilder = YamlConfigurationLoader.builder();
        loaderBuilder.defaultOptions(opts -> (opts.serializers(builder -> {
            builder.registerExact(BedrockForm.class, new BedrockFormSerializer());
            builder.registerExact(FormImage.class, new FormImageSerializer());
            builder.registerExact(CustomComponent.class, new ComponentSerializer());
        })));
    }

    /**
     * Load every config in {@link ConfigId}
     * @return false if there was a failure loading any of configurations
     */

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean loadAllConfigs() {
        for (ConfigId configId : ConfigId.VALUES) {
            try {
                if (!loadConfig(configId)) {
                    logger.severe("Configuration error in " + configId.fileName + " - Fix the issue or regenerate a new file.");
                    return false;
                }
            } catch (IOException e) {
                logger.severe("Failed to load configuration " + configId.fileName);
                e.printStackTrace();
                return false;
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
        File file = FileUtils.fileOrCopiedFromResource(new File(directory, config.fileName));
        YamlConfigurationLoader loader = loaderBuilder.file(file).build();
        Configuration mapped = loader.load().get(config.clazz);

        // todo: config translation for different config-versions

        if (mapped == null) {
            logger.severe("Failed to deserialize " + config.fileName + " to " + config.clazz + ": Mapped object returned null.");
            return false;
        }
        if (mapped.getVersion() != mapped.getDefaultVersion()) {
            logger.severe(config.fileName + " is outdated. Please back it up and regenerate a new config");
            return false;
        }
        configurations.put(config.clazz, mapped);
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T extends Configuration> T getConfig(Class<T> clazz) {
        return (T) configurations.get(clazz);
    }
}
