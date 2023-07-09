package me.offlineinvs;

import me.offlineinvs.commands.EcCommand;
import me.offlineinvs.commands.InvseeCommand;
import me.offlineinvs.commands.ListInvsCommand;
import me.offlineinvs.data.MySQL;
import me.offlineinvs.events.InventoryClickListener;
import me.offlineinvs.events.InventoryCloseListener;
import me.offlineinvs.events.JoinListener;
import me.offlineinvs.events.QuitListener;
import me.offlineinvs.manage.InventoryManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public final class OfflineInvs extends JavaPlugin {

    private static OfflineInvs instance;
    private static Inventory offlineInventorys;

    @Override
    public void onEnable() {
        instance = this;

        loadConfig();
        register(Bukkit.getPluginManager());

        MySQL.connect();
        offlineInventorys = Bukkit.createInventory(null, 9*6, Component.text("§8» §cOffline-Inventare §8«"));
        try {
            ArrayList<UUID> uuids = InventoryManager.getInvUUIDs();
            uuids.forEach(OfflineInvs::addPlayer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(PluginManager pm) {
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new QuitListener(), this);
        pm.registerEvents(new InventoryCloseListener(), this);
        pm.registerEvents(new InventoryClickListener(), this);

        this.getCommand("ec").setExecutor(new EcCommand());
        this.getCommand("invsee").setExecutor(new InvseeCommand());
        this.getCommand("listinvs").setExecutor(new ListInvsCommand());
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            try {
                InventoryManager.saveInventory(player.getInventory().getContents(), player.getUniqueId());
                InventoryManager.saveEnderChest(player.getEnderChest().getContents(), player.getUniqueId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        if(MySQL.isConnected())
            MySQL.disconnect();
    }

    private void loadConfig() {
        if(!this.getDataFolder().exists())
            this.getDataFolder().mkdir();
        File file = new File(this.getDataFolder(), "config.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                config.set("MySQL.host", "localhost");
                config.set("MySQL.port", "3306");
                config.set("MySQL.database", "");
                config.set("MySQL.username", "root");
                config.set("MySQL.password", "");

                config.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Inventory getOfflineInventorys() {
        return offlineInventorys;
    }

    public static OfflineInvs getInstance() {
        return instance;
    }

    public static void removePlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        for(int i = 0; i < getOfflineInventorys().getSize(); i++) {
            if(getOfflineInventorys().getItem(i) != null && getOfflineInventorys().getItem(i).getItemMeta().displayName().equals(Component.text("§c" + player.getName()))) {
                getOfflineInventorys().setItem(i, new ItemStack(Material.AIR));
                compact(getOfflineInventorys());
                break;
            }
        }
    }

    private static void compact(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        ItemStack[] compactedContents = new ItemStack[contents.length];
        int index = 0;

        for (ItemStack item : contents) {
            if (item != null) {
                compactedContents[index] = item;
                index++;
            }
        }

        inventory.setContents(compactedContents);
    }

    public static void addPlayer(UUID uuid) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        Bukkit.getScheduler().runTaskLater(OfflineInvs.getInstance(), () -> {
            if(op.isOnline()) return;
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(op);
            skullMeta.displayName(Component.text("§c" + op.getName()));

            Date lastSeen = new Date(op.getLastSeen());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String formattedDate = dateFormat.format(lastSeen);

            skullMeta.lore(Arrays.asList(Component.text("§7Zuletzt gespielt: §b" + formattedDate + " Uhr"), Component.text(""), Component.text("§7(§bLinksklick §7zum Öffnen des Inventares)"),
                    Component.text("§7(§bRechtsklick §7zum Öffnen der Endertruhe)")));
            skull.setItemMeta(skullMeta);

            for (int i = 0; i < getOfflineInventorys().getSize(); i++) {
                if (getOfflineInventorys().getItem(i) == null) {
                    getOfflineInventorys().setItem(i, skull);
                    break;
                }
            }
        }, 0);
    }
}
