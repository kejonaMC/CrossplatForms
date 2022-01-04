package dev.projectg.crossplatforms.form.bedrock;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.config.mapping.bedrock.FormConfig;
import dev.projectg.crossplatforms.config.mapping.bedrock.BedrockForm;
import dev.projectg.crossplatforms.reloadable.Reloadable;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BedrockFormRegistry implements Reloadable {

    /**
     * If bedrock forms are enabled. may be false if disabled in the config or if all forms failed to load.
     */
    private boolean isEnabled;
    private final Map<String, BedrockForm> enabledForms = new HashMap<>();

    public BedrockFormRegistry() {
        ReloadableRegistry.registerReloadable(this);
        isEnabled = load();
    }

    private boolean load() {
        FormConfig config = CrossplatForms.getInstance().getConfigManager().getConfig(FormConfig.class);
        enabledForms.clear();
        if (config.isEnable()) {
            enabledForms.putAll(config.getElements());
        }
        return true;
    }

    /**
     * @return True, if Java menus are enabled.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Get a BedrockForm, based off its name.
     * @param menuName The menu name
     * @return the BedrockForm, null if it doesn't exist.
     */
    @Nullable
    public BedrockForm getMenu(@Nonnull String menuName) {
        Objects.requireNonNull(menuName);
        return enabledForms.get(menuName);
    }

    @Override
    public boolean reload() {
        isEnabled = load();
        return true;
    }
}
