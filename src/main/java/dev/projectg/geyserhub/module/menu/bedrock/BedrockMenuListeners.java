package dev.projectg.geyserhub.module.menu.bedrock;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.module.menu.AccessItem;
import dev.projectg.geyserhub.module.menu.java.JavaMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.geysermc.floodgate.api.FloodgateApi;

public class BedrockMenuListeners implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().isSimilar(AccessItem.getItem())) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                    BedrockMenu.getInstance().sendForm(FloodgateApi.getInstance().getPlayer(player.getUniqueId()), BedrockMenu.DEFAULT);
                } else {
                    JavaMenu.openMenu(player, GeyserHubMain.getInstance().getConfig());
                }
            }
        }
    }
}
