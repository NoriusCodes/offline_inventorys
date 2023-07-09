package me.offlineinvs.events;

import me.offlineinvs.commands.EcCommand;
import me.offlineinvs.commands.InvseeCommand;
import me.offlineinvs.manage.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryCloseListener implements Listener {

    private Map<Player, UUID> handlers;

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (!EcCommand.enderchest.isEmpty()) {
            handlers = new ConcurrentHashMap<>(EcCommand.handlers);
            UUID playerUUID = handlers.get(player);
            if (event.getInventory().equals(EcCommand.enderchest.get(playerUUID))) {
                try {
                    InventoryManager.saveEnderChest(event.getInventory().getContents(), playerUUID);
                    EcCommand.handlers.remove(player);
                    EcCommand.enderchest.remove(playerUUID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (!InvseeCommand.inventorys.isEmpty()) {
            handlers = new ConcurrentHashMap<>(InvseeCommand.handlers);
            UUID playerUUID = handlers.get(player);
            if (event.getInventory().equals(InvseeCommand.inventorys.get(playerUUID))) {
                try {
                    InventoryManager.saveInventory(event.getInventory().getContents(), playerUUID);
                    InvseeCommand.handlers.remove(player);
                    InvseeCommand.inventorys.remove(playerUUID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
