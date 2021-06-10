package dev.projectg.geyserhub.module.menu.bedrock;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.Reloadable;
import dev.projectg.geyserhub.ReloadableRegistry;
import dev.projectg.geyserhub.SelectorLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BedrockMenu implements Reloadable {

    private static BedrockMenu INSTANCE;

    private boolean isEnabled;
    private final Map<String, BedrockForm> enabledForms = new HashMap<>();

    public static BedrockMenu getInstance() {
        return INSTANCE;
    }

    public BedrockMenu() {
        ReloadableRegistry.registerReloadable(this);
        isEnabled = load();
        INSTANCE = this;
    }

    private boolean load() {
        FileConfiguration config = GeyserHubMain.getInstance().getConfig();
        SelectorLogger logger = SelectorLogger.getLogger();

        enabledForms.clear();

        if (config.contains("Bedrock-Selector", true) && config.isConfigurationSection("Bedrock-Selector")) {
            ConfigurationSection selectorSection = config.getConfigurationSection("Bedrock-Selector");
            assert selectorSection != null;

            if (selectorSection.contains("Enable", true) && selectorSection.isBoolean("Enable")) {
                if (selectorSection.getBoolean("Enable")) {
                    if (selectorSection.contains("Forms", true) && selectorSection.isConfigurationSection("Forms")) {
                        ConfigurationSection forms = selectorSection.getConfigurationSection("forms");
                        assert forms != null;

                        boolean noSuccess = true;
                        for (String entry : forms.getKeys(false)) {
                            if (!forms.isConfigurationSection(entry)) {
                                logger.warn("Bedrock form with name " + entry + " is being skipped because it is not a configuration section");
                                continue;
                            }
                            ConfigurationSection formInfo = forms.getConfigurationSection(entry);
                            assert formInfo != null;
                            BedrockForm form = new BedrockForm(formInfo);
                            if (form.isEnabled()) {
                                enabledForms.put(entry, form);
                                noSuccess = false;
                            }
                        }

                        if (noSuccess) {
                            isEnabled = false;
                            logger.warn("Failed to ALL bedrock forms, due to configuration error.");
                        } else {
                            isEnabled = true;
                            logger.info("Valid Bedrock forms are: " + enabledForms.keySet());
                            return true;
                        }
                    }
                } else {
                    logger.debug("Not enabling bedrock forms because it is disabled in the config.");
                    isEnabled = false;
                }
            } else {
                logger.warn("Not enabling bedrock forms because the Enable value is not present in the config.");
            }
        } else {
            logger.warn("Not enabling bedrock forms because the whole configuration section is not present.");
        }
        return false;
    }

    public void sendForm(@Nonnull FloodgatePlayer player) {
        enabledForms.get("parent").sendForm(player);
    }

    public void sendForm(@Nonnull FloodgatePlayer player, @Nonnull String form) {
        enabledForms.get(form).sendForm(player);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean reload() {
        isEnabled = load();
        return isEnabled;
    }
}
