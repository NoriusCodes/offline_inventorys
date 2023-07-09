package me.offlineinvs.manage;

import me.offlineinvs.data.MySQL;
import me.offlineinvs.util.Base64;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class InventoryManager {

    public static void saveEnderChest(ItemStack[] items, UUID uuid) throws SQLException {
        String itemsToSave = Base64.itemStackArrayToBase64(items);
        Connection connection = MySQL.getConnection();
        PreparedStatement ps = connection.prepareStatement("INSERT INTO player_enderchests(uuid, items) VALUES(?, ?) ON DUPLICATE KEY UPDATE items = ?");
        ps.setString(1, uuid.toString());
        ps.setString(2, itemsToSave);
        ps.setString(3, itemsToSave);
        ps.execute();
        ps.close();
    }

    public static void saveInventory(ItemStack[] items, UUID uuid) throws SQLException {
        String itemsToSave = Base64.itemStackArrayToBase64(items);
        Connection connection = MySQL.getConnection();
        PreparedStatement ps = connection.prepareStatement("INSERT INTO player_inventorys(uuid, items) VALUES(?, ?) ON DUPLICATE KEY UPDATE items = ?");
        ps.setString(1, uuid.toString());
        ps.setString(2, itemsToSave);
        ps.setString(3, itemsToSave);
        ps.execute();
        ps.close();
    }

    public static ItemStack[] getEnderChest(UUID uuid) throws SQLException, IOException {
        Connection connection = MySQL.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT items FROM player_enderchests WHERE uuid = ?");
        ps.setString(1, uuid.toString());
        ResultSet resultSet = ps.executeQuery();

        if(resultSet.next()) {
            String items = resultSet.getString("items");
            ItemStack[] loadedItems = Base64.itemStackArrayFromBase64(items);
            return loadedItems;
        }
        return new ItemStack[0];
    }

    public static ItemStack[] getInventory(UUID uuid) throws SQLException, IOException {
        Connection connection = MySQL.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT items FROM player_inventorys WHERE uuid = ?");
        ps.setString(1, uuid.toString());
        ResultSet resultSet = ps.executeQuery();

        if(resultSet.next()) {
            String items = resultSet.getString("items");
            ItemStack[] loadedItems = Base64.itemStackArrayFromBase64(items);
            return loadedItems;
        }
        return new ItemStack[0];
    }

    public static ArrayList<UUID> getInvUUIDs() throws SQLException {
        Connection connection = MySQL.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM player_inventorys");
        ResultSet rs = ps.executeQuery();

        ArrayList<UUID> uuids = new ArrayList<>();

        while (rs.next())
            uuids.add(UUID.fromString(rs.getString("uuid")));
        return uuids;
    }

    public static boolean existsEnderChest(UUID uuid) throws SQLException {
        Connection connection = MySQL.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM player_enderchests WHERE uuid = ?");
        ps.setString(1, uuid.toString());
        ResultSet resultSet = ps.executeQuery();
        if(resultSet.next())
            return true;
        return false;
    }

    public static boolean existsInventory(UUID uuid) throws SQLException {
        Connection connection = MySQL.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM player_inventorys WHERE uuid = ?");
        ps.setString(1, uuid.toString());
        ResultSet resultSet = ps.executeQuery();
        if(resultSet.next())
            return true;
        return false;
    }
}
