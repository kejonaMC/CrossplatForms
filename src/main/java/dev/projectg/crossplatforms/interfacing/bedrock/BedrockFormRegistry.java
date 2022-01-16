package dev.projectg.crossplatforms.interfacing.bedrock;

import dev.projectg.crossplatforms.CrossplatForms;
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
    private boolean enabled;
    private final Map<String, BedrockForm> forms = new HashMap<>();

    public BedrockFormRegistry() {
        ReloadableRegistry.registerReloadable(this);
        load();
    }

    private void load() {
        CrossplatForms plugin = CrossplatForms.getInstance();
        FormConfig config = plugin.getConfigManager().getConfig(FormConfig.class);
        forms.clear();
        if (enabled = config.isEnable()) {
            for (String identifier : config.getForms().keySet()) {
                BedrockForm form = config.getForms().get(identifier);

                form.generatePermissions(config);
                for (Permission entry : form.getPermissions().values()) {
                    plugin.getServerHandler().registerPermission(entry);
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
                    CrossplatForms.getInstance().getServerHandler().unregisterPermission(permission.key());
                }
            }
        }

        load();
        return true;
    }
}
