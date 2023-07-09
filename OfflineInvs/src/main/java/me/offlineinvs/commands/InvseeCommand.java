package me.offlineinvs.commands;

import me.offlineinvs.manage.InventoryManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InvseeCommand implements CommandExecutor {

    public static Map<Player, UUID> handlers = new ConcurrentHashMap<>();
    public static Map<UUID, Inventory> inventorys = new ConcurrentHashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!player.hasPermission("commands.invsee")) {
            player.sendMessage("§cDazu hast du keine Berechtigung!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§cBenutze: §b/invsee <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target != null) {
            player.openInventory(target.getInventory());
            return true;
        }
        OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
        player.sendMessage("§7Inventar von §b" + op.getName() + " §7lädt...");

        try {
            if (!InventoryManager.existsInventory(op.getUniqueId())) {
                player.sendMessage("§cInventar nicht in der Datenbank vorhanden!");
                return true;
            }

            Inventory targetInventory = Bukkit.createInventory(null, InventoryType.PLAYER, Component.text("Inventar von " + op.getName()));
            targetInventory.setContents(InventoryManager.getInventory(op.getUniqueId()));
            player.openInventory(targetInventory);

            UUID playerUUID = op.getUniqueId();
            handlers.put(player, playerUUID);
            inventorys.put(playerUUID, targetInventory);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
