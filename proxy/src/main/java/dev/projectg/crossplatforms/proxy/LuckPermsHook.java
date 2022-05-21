package dev.projectg.crossplatforms.proxy;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.WeightNode;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

public class LuckPermsHook implements PermissionHook {

    private static final String DEFAULT_NAME = "default";
    private static final WeightNode DEFAULT_WEIGHT = WeightNode.builder(0).build();

    private final GroupManager groupManager = LuckPermsProvider.get().getGroupManager();
    private final Logger logger = Logger.getLogger();

    @Override
    public void registerPermission(String key, @Nullable String description, PermissionDefault def) {
        groupManager.loadGroup(DEFAULT_NAME).thenAcceptAsync(defaultGroup -> {
            Group group = defaultGroup.orElse(null);
            if (group == null) {
                if (def == PermissionDefault.OP) {
                    // We clear any permissions with a default of OP.
                    // If the group doesn't exist, there is nothing to clear.
                    return;
                }

                try {
                    group = groupManager.createAndLoadGroup(DEFAULT_NAME).get();
                    group.data().add(DEFAULT_WEIGHT);
                    logger.debug("Created LuckPerms group: " + DEFAULT_NAME);
                } catch (InterruptedException | ExecutionException e) {
                    Logger.getLogger().severe("Failed to register create and load group: " + DEFAULT_NAME);
                    e.printStackTrace();
                    return;
                }
            }

            clearPermission(group, key);
            if (def != PermissionDefault.OP) {
                // we simply clear for OP. TRUE and FALSE should have any existing permissions with the same key
                // cleared and then re-added with the correct values
                setPermission(group, key, def.asBoolean());
            }

            groupManager.saveGroup(group);
        });
    }

    private void clearPermission(Group group, String key) {
        group.data().clear(node -> node.getKey().equals(key));
    }

    private void setPermission(Group group, String key, boolean value) {
        group.data().add(PermissionNode.builder(key).value(value).build());
    }
}
