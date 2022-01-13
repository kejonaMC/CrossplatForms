package dev.projectg.crossplatforms.form.bedrock;

import dev.projectg.crossplatforms.CrossplatForms;
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
    private boolean enabled;
    private final Map<String, BedrockForm> forms = new HashMap<>();

    public BedrockFormRegistry() {
        ReloadableRegistry.registerReloadable(this);
        load();
    }

    private void load() {
        FormConfig config = CrossplatForms.getInstance().getConfigManager().getConfig(FormConfig.class);
        forms.clear();
        if (enabled = config.isEnable()) {
            forms.putAll(config.getForms());
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
        load();
        return true;
    }
}
