package dev.projectg.crossplatforms.proxy;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.WeightNode;
import net.luckperms.api.util.Tristate;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class LuckPermsHook implements PermissionHook {

    private static final String DEFAULT_NAME = "default";
    private static final WeightNode DEFAULT_WEIGHT = WeightNode.builder(0).build();

    private static final String OPERATOR_KEY = "*";
    private static final PermissionNode OPERATOR = PermissionNode.builder(OPERATOR_KEY).build();
    private static final PermissionNode OPERATOR_FALSE = OPERATOR.toBuilder().value(false).build();

    private final LuckPerms luckPerms = LuckPermsProvider.get();
    private final UserManager userManager = luckPerms.getUserManager();
    private final GroupManager groupManager = luckPerms.getGroupManager();
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

            clearPermission(group.data(), key);
            if (def != PermissionDefault.OP) {
                // we simply clear for OP. TRUE and FALSE should have any existing permissions with the same key
                // cleared and then re-added with the correct values
                setPermission(group.data(), key, def.asBoolean());
            }

            groupManager.saveGroup(group);
        });
    }

    private void clearPermission(NodeMap data, String key) {
        data.clear(node -> node.getKey().equals(key));
    }

    private void setPermission(NodeMap data, String key, boolean value) {
        data.add(PermissionNode.builder(key).value(value).build());
    }

    @Override
    public void executeWithOperator(UUID uuid, Runnable runnable) {
        User user = userManager.getUser(uuid);
        if (user == null) {
            logger.severe("Failed to elevate " + uuid + " for a task because LuckPerms could not find a respective User");
            runnable.run();
            return;
        }

        Tristate operator = user.getCachedData().getPermissionData().checkPermission(OPERATOR_KEY);

        if (operator == Tristate.UNDEFINED) {
            user.data().add(OPERATOR); // add operator

            try {
                runnable.run();
            } finally {
                // remove operator and then allow any throwables to continue
                user.data().clear(n -> n.getKey().equals(OPERATOR_KEY));
            }
        } else if (operator == Tristate.TRUE) {
            // already has it
           runnable.run();
        } else if (operator == Tristate.FALSE) {
            clearPermission(user.data(), OPERATOR_KEY); // remove false key
            user.data().add(OPERATOR); // add true key
            userManager.saveUser(user).join();
            // we need to wait for this to finish because it is expected the runnable is executed within this method

            try {
                runnable.run();
            } finally {
                clearPermission(user.data(), OPERATOR_KEY); // remove true key we just added
                user.data().add(OPERATOR_FALSE); // set explicitly false, as it was before
                userManager.saveUser(user);
            }
        }
    }
}
