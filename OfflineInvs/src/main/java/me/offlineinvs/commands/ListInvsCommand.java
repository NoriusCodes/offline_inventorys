package me.offlineinvs.commands;

import me.offlineinvs.OfflineInvs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ListInvsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!player.hasPermission("commands.list")) {
            player.sendMessage("§cDazu hast du keine Berechtigung!");
            return true;
        }

        if (args.length != 0) {
            player.sendMessage("§cBitte benutze: §b/listinvs");
        }

        if(OfflineInvs.getOfflineInventorys().isEmpty())
            player.sendMessage("§cAlle Spieler, deren Inventare gespeichert wurden, sind online.");
        else
            player.openInventory(OfflineInvs.getOfflineInventorys());
        return true;
    }
}
