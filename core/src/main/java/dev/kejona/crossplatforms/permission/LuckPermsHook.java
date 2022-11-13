package dev.kejona.crossplatforms.permission;

import dev.kejona.crossplatforms.Logger;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.WeightNode;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class LuckPermsHook implements Permissions {

    private static final String DEFAULT_NAME = "default";
    private static final WeightNode DEFAULT_WEIGHT = WeightNode.builder(0).build();

    private final GroupManager groupManager = LuckPermsProvider.get().getGroupManager();
    private final Logger logger = Logger.get();

    @Override
    public void registerPermissions(Collection<Permission> permissions) {
        groupManager.loadGroup(DEFAULT_NAME).thenAcceptAsync(defaultGroup -> {
            Group group = defaultGroup.orElse(null);
            if (group == null) {
                try {
                    group = groupManager.createAndLoadGroup(DEFAULT_NAME).get();
                    group.data().add(DEFAULT_WEIGHT);
                    logger.debug("Created LuckPerms group: " + DEFAULT_NAME);
                } catch (InterruptedException | ExecutionException e) {
                    Logger.get().severe("Failed to register create and load group: " + DEFAULT_NAME);
                    e.printStackTrace();
                    return;
                }
            }

            // Clear existing keys
            clearPermissions(group, permissions.stream().map(Permission::key).collect(Collectors.toSet()));

            logger.debug("Registering permissions to LP:");
            for (Permission perm : permissions) {
                String key = perm.key();
                PermissionDefault def = perm.defaultPermission();

                logger.debug("\t" + key + " : " + def);
                if (def != PermissionDefault.OP) {
                    setPermission(group, perm.key(), def.asBoolean());
                }
            }

            groupManager.saveGroup(group);
        });
    }

    private void clearPermissions(Group group, Collection<String> keys) {
        group.data().clear(node -> keys.contains(node.getKey()));
    }

    private void setPermission(Group group, String key, boolean value) {
        group.data().add(PermissionNode.builder(key).value(value).build());
    }
}
