package me.offlineinvs.events;

import me.offlineinvs.OfflineInvs;
import me.offlineinvs.manage.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        try {
            InventoryManager.saveInventory(player.getInventory().getContents(), player.getUniqueId());
            InventoryManager.saveEnderChest(player.getEnderChest().getContents(), player.getUniqueId());

            OfflineInvs.addPlayer(player.getUniqueId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
