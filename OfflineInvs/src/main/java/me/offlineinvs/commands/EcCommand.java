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

public class EcCommand implements CommandExecutor {

    public static Map<Player, UUID> handlers = new ConcurrentHashMap<>();
    public static Map<UUID, Inventory> enderchest = new ConcurrentHashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        switch (args.length) {
            case 0:
                player.openInventory(player.getEnderChest());
                break;
            case 1:
                if (!player.hasPermission("ec.other")) {
                    player.sendMessage("§cDazu hast du keine Berechtigung!");
                    break;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
                    player.sendMessage("§7Enderchest von §b" + op.getName() + " §7lädt...");

                    try {
                        if (!InventoryManager.existsEnderChest(op.getUniqueId())) {
                            player.sendMessage("§cEnderchest nicht in der Datenbank vorhanden!");
                            return true;
                        }

                        Inventory targetEnderchest = Bukkit.createInventory(null, InventoryType.CHEST, Component.text("Enderchest von " + op.getName()));
                        targetEnderchest.setContents(InventoryManager.getEnderChest(op.getUniqueId()));
                        player.openInventory(targetEnderchest);

                        UUID playerUUID = op.getUniqueId();
                        handlers.put(player, playerUUID);
                        enderchest.put(playerUUID, targetEnderchest);
                    } catch (SQLException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                player.openInventory(target.getEnderChest());
                break;
            default:
                player.sendMessage("§cBenutze: §b/ec <Spieler>");
                break;
        }
        return true;
    }
}
