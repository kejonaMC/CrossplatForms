package dev.projectg.crossplatforms.interfacing.bedrock;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.permission.Permission;
import dev.projectg.crossplatforms.reloadable.Reloadable;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class BedrockFormRegistry implements Reloadable {

    /**
     * If bedrock forms are enabled. may be false if disabled in the config or if all forms failed to load.
     */
    private boolean enabled = false;
    private final Map<String, BedrockForm> forms = new HashMap<>();

    private final ConfigManager configManager;
    private final ServerHandler serverHandler;

    public BedrockFormRegistry(ConfigManager configManager, ServerHandler serverHandler) {
        this.configManager = configManager;
        this.serverHandler = serverHandler;
        ReloadableRegistry.register(this);
        load();
    }

    private void load() {
        forms.clear();

        if (configManager.getConfig(FormConfig.class).isEmpty()) {
            enabled = false;
            Logger.getLogger().warn("Form config is not present, not enabling forms.");
            return;
        }

        FormConfig config = configManager.getConfig(FormConfig.class).get();
        enabled = config.isEnable();
        if (enabled) {
            for (String identifier : config.getForms().keySet()) {
                BedrockForm form = config.getForms().get(identifier);
                forms.put(identifier, form);

                form.generatePermissions(config);
                for (Permission entry : form.getPermissions().values()) {
                    serverHandler.registerPermission(entry);
                }
            }
        }
    }

    /**
     * Get a BedrockForm, based off its name.
     * @param formName The menu name
     * @return the BedrockForm, null if it doesn't exist.
     */
    @Nullable
    public BedrockForm getForm(@Nonnull String formName) {
        Objects.requireNonNull(formName);
        return forms.get(formName);
    }

    @Override
    public boolean reload() {

        // Unregister permissions
        if (enabled) {
            for (Interface form : forms.values()) {
                for (Permission permission : form.getPermissions().values()) {
                    serverHandler.unregisterPermission(permission.key());
                }
            }
        }

        load();
        return true;
    }
}
