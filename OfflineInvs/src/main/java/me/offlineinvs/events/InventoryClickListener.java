package me.offlineinvs.events;

import me.offlineinvs.OfflineInvs;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null) return;
        Player player = (Player) event.getWhoClicked();
        if(event.getView().getTopInventory().equals(OfflineInvs.getOfflineInventorys())) {
            event.setCancelled(true);
            if(!event.getClickedInventory().equals(OfflineInvs.getOfflineInventorys())) return;
            ItemStack item = event.getCurrentItem();
            OfflinePlayer op = Bukkit.getOfflinePlayer(item.getItemMeta().getDisplayName().replace("§c", ""));
            switch (event.getClick()) {
                case LEFT:
                    Bukkit.dispatchCommand(player, "invsee " + op.getName());
                    break;
                case RIGHT:
                    Bukkit.dispatchCommand(player, "ec " + op.getName());
                    break;
                default:
                    break;
            }
        }
    }
}
