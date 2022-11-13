package dev.kejona.crossplatforms.interfacing.bedrock;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.permission.Permission;
import dev.kejona.crossplatforms.permission.Permissions;
import dev.kejona.crossplatforms.reloadable.Reloadable;
import dev.kejona.crossplatforms.reloadable.ReloadableRegistry;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BedrockFormRegistry implements Reloadable {

    private final ConfigManager configManager;
    private final Permissions permissions;

    @Getter
    private final Map<String, BedrockForm> forms = new HashMap<>();

    /**
     * If bedrock forms are enabled. may be false if disabled in the config or if all forms failed to load.
     */
    @Getter
    private boolean enabled = false;

    public BedrockFormRegistry(ConfigManager configManager, Permissions permissions) {
        this.configManager = configManager;
        this.permissions = permissions;
        ReloadableRegistry.register(this);
        load();
    }

    private void load() {
        forms.clear();

        if (!configManager.getConfig(FormConfig.class).isPresent()) {
            enabled = false;
            Logger.get().warn("Form config is not present, not enabling forms.");
            return;
        }

        FormConfig config = configManager.getConfig(FormConfig.class).get();
        enabled = config.isEnable();
        if (enabled) {
            Set<Permission> permissions = new HashSet<>();

            for (String identifier : config.getForms().keySet()) {
                BedrockForm form = config.getForms().get(identifier);
                forms.put(identifier, form);

                form.generatePermissions(config);
                permissions.addAll(form.getPermissions().values());
            }

            this.permissions.registerPermissions(permissions);
        }
    }

    @Override
    public boolean reload() {
        load();
        return true;
    }

    /**
     * Get a BedrockForm, based off its name.
     * @param formName The menu name
     * @return the BedrockForm, null if it doesn't exist.
     */
    @Nullable
    public BedrockForm getForm(@Nullable String formName) {
        return forms.get(formName);
    }
}
