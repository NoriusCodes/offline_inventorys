package me.offlineinvs.events;

import me.offlineinvs.OfflineInvs;
import me.offlineinvs.manage.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.sql.SQLException;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            if(InventoryManager.existsInventory(player.getUniqueId())) {
                player.getInventory().setContents(InventoryManager.getInventory(player.getUniqueId()));
            }
            if(InventoryManager.existsEnderChest(player.getUniqueId())) {
                player.getEnderChest().setContents(InventoryManager.getEnderChest(player.getUniqueId()));
            }

            OfflineInvs.removePlayer(player.getUniqueId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
